package org.rekex.parser;

import org.rekex.annotype.AnnoType;
import org.rekex.annotype.ArrayType;
import org.rekex.annotype.ClassType;
import org.rekex.annotype.PrimitiveType;
import org.rekex.common_util.SwitchOnType;
import org.rekex.grammar.*;
import org.rekex.parser.ParseResult.Node;
import org.rekex.spec.Not;
import org.rekex.spec.Peek;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

// do not optimize this code for the sake of performance:
//   this impl is written to demonstrate and clarify semantics;
//   to be compared with other impls for behavior and performance.

/**
 * A reference implementation of PEG parser.
 * Not to be used in production.
 */
public class ReferencePegParser<T> implements PegParser<T>
{
    final Grammar grammar;
    final AnnoType rootType;

    public ReferencePegParser(Grammar grammar, AnnoType rootType)
    {
        this.grammar = grammar;
        this.rootType = rootType;
    }

    public static <T> ReferencePegParser<T> of(AnnoType rootType, Class<?> ctorCatalog)
    {
        try
        {
            var grammar = Grammar.deriveFrom(List.of(rootType), ctorCatalog);
            return new ReferencePegParser<>(grammar, rootType);
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }


    @Override
    public ParseResult<T> parse(CharSequence chars, int start, int end)
    {
        Integer ruleId = grammar.typeToId().get(rootType);
        Input input = new Input(chars, start, end, new Path(List.of()));

        Result result;
        try
        {
            result = match(ruleId, input, -1);
        }
        catch (FatalEx fatalEx)
        {
            ArrayList<Node> stack = new ArrayList<>(fatalEx.path.nodes);
            Collections.reverse(stack);
            return new ParseResult.Fatal<T>(fatalEx.position, fatalEx.cause, stack);
        }

        if(result instanceof OK ok)
        {
            @SuppressWarnings("unchecked")
            T value = (T)ok.obj;
            if(ok.pos==end)
                return new ParseResult.Full<>(value);
            else
                return new ParseResult.Partial<>(value, ok.pos);
        }
        else
        {
            Fail fail = (Fail)result;
            ArrayList<Node> stack = new ArrayList<>(fail.path.nodes);
            Collections.reverse(stack);
            String failMsg = failMsg(fail.reason, fail.info);
            return new ParseResult.Fail<>(fail.pos, failMsg, stack);
        }
    }

    record Path(List<Node> nodes)
    {
        Path append(GrammarRule rule, int start, int subIndex)
        {
            ArrayList<Node> newList = new ArrayList<>(nodes);
            newList.add(new Node(rule.datatype(), start, subIndex));
            return new Path(newList);
        }
    }

    record Input(CharSequence chars, int start, int end, Path path)
    {

    }

    sealed interface Result
    {
        Fail maxFail();
    }
    record OK(Object obj, int pos, Fail maxFail) implements Result
    {

    }
    record Fail(int pos, Path path, int reason, Object info) implements Result
    {
        public Fail maxFail(){ return this; }

        public static Fail max(Fail f1, Fail f2)
        {
            if(f1==null) return f2;
            if(f2==null) return f1;
            return f2.pos>f1.pos ? f2 : f1;
        }
    }

    static class FatalEx extends Exception
    {
        final int position;
        Exception cause;
        final Path path;
        FatalEx(int position, Exception cause, Path path)
        {
            super(null, null, false, false);
            this.position = position;
            this.cause = cause;
            this.path = path;
        }
    }

    static final int failReason_illegal_arg = PegParserTemplate.failReason_illegal_arg;
    static final int failReason_neg = PegParserTemplate.failReason_neg;
    static final int failReason_regex = PegParserTemplate.failReason_regex;
    static final int failReason_regex_group = PegParserTemplate.failReason_regex_group;
    String failMsg(int reason, Object info)
    {
        return PegParserTemplate.failMsg(reason, info);
    }


    interface Handler
    {
        Result apply(Input input) throws FatalEx;
    }

    final Function<GrammarRule, Handler> matchRule =
        new SwitchOnType<GrammarRule, Handler>()
            .on(AltRule.class,
                rule -> input -> matchAlt(rule, input)
            )
            .on(ConcatRule.class,
                rule -> input -> matchConcat(rule, input)
            )
            .on(RepeatRule.class,
                rule -> input -> matchRepeat(rule, input)
            )
            .on(PeekRule.class,
                rule -> input -> matchPeek(rule, input)
            )
            .on(NegRule.class,
                rule -> input -> matchNeg(rule, input)
            )
            .on(RegexRule.class,
                rule -> input -> matchRegex(rule, input)
            )
            .complete(GrammarRule.class);

    Result match(Integer ruleId, Input input, int subIndex) throws FatalEx
    {
        var rule = grammar.idToRule().get(ruleId);
        Path path = input.path.append(rule, input.start, subIndex);
        input = new Input(input.chars, input.start, input.end, path);
        return matchRule.apply(rule).apply(input);
    }

    // ==========================================================================================

    Result matchAlt(AltRule rule, Input input) throws FatalEx
    {
        Fail maxFail = null;
        for(int subIndex=0; subIndex<rule.subRuleIds().size(); subIndex++)
        {
            Integer subId = rule.subRuleIds().get(subIndex);
            var result = match(subId, input, subIndex);
            maxFail = Fail.max(maxFail, result.maxFail());
            if(result instanceof OK ok)
                return new OK(ok.obj, ok.pos, maxFail);
        }

        if(maxFail==null) // we don't produce 0-arity alt rules
            throw new AssertionError();

        return maxFail;
    }

    Result matchConcat(ConcatRule rule, Input input0) throws FatalEx
    {
        Fail maxFail = null;
        int N = rule.subRuleIds().size();
        Object[] args = new Object[N];
        Input inputX = input0;
        for(int subIndex=0; subIndex<N; subIndex++)
        {
            Integer subId = rule.subRuleIds().get(subIndex);
            var result = match(subId, inputX, subIndex);
            maxFail = Fail.max(maxFail, result.maxFail());

            if(result instanceof Fail)
                return maxFail;

            OK ok = (OK)result;
            args[subIndex] = ok.obj;
            inputX = new Input(inputX.chars, ok.pos, inputX.end, input0.path);
        }

        // fail pos is set at the end of the matched region.
        // doubtful. but the start pos is available in the path anyways.
        int failPos = inputX.start;
        try
        {
            Object obj = tryInstantiate(failPos, input0.path, rule.instantiator(), args);
            return new OK(obj, inputX.start, maxFail);
        }
        catch (IllegalArgumentException ex) // not fatal; fail this rule.
        {
            Fail thisFail = new Fail(failPos, input0.path, failReason_illegal_arg, ex);
            return Fail.max(maxFail, thisFail);
        }
    }



    Result matchRepeat(RepeatRule rule, Input input0)  throws FatalEx
    {
        Fail maxFail = null;
        ArrayList<Object> args = new ArrayList<>();
        // must match at least `min` args
        Input inputX = input0;
        while(args.size()<rule.min())
        {
            var result = match(rule.subRuleId(), inputX, args.size());
            maxFail = Fail.max(maxFail, result.maxFail());
            if(result instanceof Fail)
                return maxFail;

            OK ok = (OK)result;
            args.add(ok.obj);
            inputX = new Input(inputX.chars, ok.pos, inputX.end, input0.path);
        }

        // we have collected enough args. we try to greedily match more args.
        while(args.size()<rule.max())
        {
            var result = match(rule.subRuleId(), inputX, args.size());
            maxFail = Fail.max(maxFail, result.maxFail());
            if(result instanceof Fail)
                break;

            OK ok = (OK)result;
            if(ok.pos==inputX.start) // matched, but consumed nothing
            {
                // since we've had enough args, no point to add this one, or continuing
                break;
            }
            else
            {
                args.add(ok.obj);
                inputX = new Input(inputX.chars, ok.pos, inputX.end, input0.path);
            }
        }

        if(rule.datatype() instanceof ClassType classType)
        {
            assert classType.clazz()==List.class;
            return new OK(args, inputX.start, maxFail);
        }
        else if(rule.datatype() instanceof ArrayType arrayType)
        {
            Object array = createArray(args, arrayType);
            return new OK(array, inputX.start, maxFail);
        }
        else
        {
            throw new AssertionError();
        }

    }


    Result matchPeek(PeekRule rule, Input input) throws FatalEx
    {
        var result = match(rule.subRuleId(), input, -1);
        if(result instanceof Fail fail)
            return fail;

        // subrule succeeded: this rule does not consume.
        // fail info in subrule are not preserved by this rule.
        OK ok = (OK)result;
        return new OK(new Peek<>(ok.obj), input.start, null);
    }

    Result matchNeg(NegRule rule, Input input) throws FatalEx
    {
        // fail info in subrule are not preserved by this rule in any case.
        // not sure how to handle FatalEx from the subrule
        var result = match(rule.subRuleId(), input, -1);
        if(result instanceof Fail)
            return new OK(new Not<>(), input.start, null);
        else
            return new Fail(input.start, input.path, failReason_neg, rule.subRuleId());
    }


    Result matchRegex(RegexRule rule, Input input) throws FatalEx
    {
        var regex = rule.regex();
        var pattern = Pattern.compile(regex.value(), regex.flags());

        var matcher = pattern.matcher(input.chars);
        matcher.region(input.start, input.end);
        // if datatype is char, and the regex group matches more than one char,
        // it's a mistake in rule design. match error or fatal error? fatal?
        // we can't limit end=start+1 here, because group 0 may match more chars.

        boolean matched = matcher.lookingAt(); // this is greedy by default
        if(!matched)
            return new Fail(input.start, input.path, failReason_regex, null);

        var g = regex.group();
        int gStart = matcher.start(g);
        int gEnd = matcher.end(g);
        if(gStart==-1)
            return new Fail(input.start, input.path, failReason_regex_group, null);

        int g0End = matcher.end(0); // consume group 0

        if(rule.instantiator()==null)
            return tryGetCharOrStr(rule, input.chars, gStart, gEnd, g0End, input.path);

        // just StaticField
        Object obj = tryInstantiate(input.start, input.path, rule.instantiator());
        return new OK(obj, g0End, null);
    }


    // ==========================================================================================

    // caller should treat IllegalArgumentException as Fail
    static Object tryInstantiate(int fatalPos, Path path, Instantiator itor, Object... args)
        throws IllegalArgumentException, FatalEx
    {
        try
        {
            Object obj;
            if(itor instanceof Instantiator.NewInstance x)
            {
                x.constructor().setAccessible(true);
                obj = x.constructor().newInstance(args);
            }
            else if(itor instanceof Instantiator.StaticMethod x)
            {
                x.method().setAccessible(true);
                obj = x.method().invoke(null, args);
            }
            else if(itor instanceof Instantiator.StaticField x)
            {
                x.field().setAccessible(true);
                obj = x.field().get(null);
            }
            else
            {
                throw new AssertionError();
            }
            return obj;
        }
        catch (InvocationTargetException e)
        {
            try
            {
                throw e.getTargetException();
            }
            catch(IllegalArgumentException ex)
            {
                throw ex;
            }
            catch(Exception ex)
            {
                throw new FatalEx(fatalPos, ex, path);
            }
            catch (Error ex)
            {
                throw ex; // don't wrap it
            }
            catch(Throwable ex)
            {
                // neither Error nor Exception; allowed by Java by nobody does it.
                throw new FatalEx(fatalPos, new Exception(ex), path);
            }
        }
        catch (Exception e)
        {
            throw new Error("reflection error", e);
        }
    }

    static Object createArray(ArrayList<Object> args, ArrayType arrayType)
    {
        int[] dimensions = {args.size()};
        AnnoType compoType = arrayType.componentType();
        while(compoType instanceof ArrayType at2)
        {
            dimensions = Arrays.copyOf(dimensions, dimensions.length+1);
            compoType = at2.componentType();
        }
        Class<?> compoClass;
        if(compoType instanceof PrimitiveType pt)
            compoClass = pt.clazz();
        else if(compoType instanceof ClassType ct)
            compoClass = ct.clazz();
        else // type var; huh?
            throw new AssertionError("unsupported array type: "+ arrayType);

        Object array = Array.newInstance(compoClass, dimensions);
        for(int i = 0; i< args.size(); i++)
            Array.set(array, i, args.get(i));
        return array;
    }

    static OK tryGetCharOrStr(RegexRule rule, CharSequence chars,
                                  int start, int end, int g0End, Path path) throws FatalEx
    {
        if(rule.datatype() instanceof PrimitiveType pt)
        {
            if(pt.clazz()==char.class)
                return tryGetSingleChar(rule, chars, start, end, g0End, path);
            else if(pt.clazz()==int.class)
                return tryGetSingleCodePoint(rule, chars, start, end, g0End, path);
            else
                throw new AssertionError();
        }
        else if(rule.datatype() instanceof ClassType ct)
        {
            if(ct.clazz()==Character.class)
                return tryGetSingleChar(rule, chars, start, end, g0End, path);
            else if(ct.clazz()==Integer.class)
                return tryGetSingleCodePoint(rule, chars, start, end, g0End, path);
            else if(ct.clazz()==String.class)
                return new OK(chars.subSequence(start, end).toString(), g0End, null);
            else
                throw new AssertionError();
        }
        else
        {
            throw new AssertionError();
        }
    }

    static OK tryGetSingleChar(RegexRule rule, CharSequence chars,
                                   int start, int end, int g0End, Path path) throws FatalEx
    {
        var ok = tryGetSingleCodePoint(rule, chars, start, end, g0End, path);
        Integer x = (Integer)ok.obj;
        if(x>0xFFFF)
        {
            // programming mistake in the regex, or an unexpected input
            String msg = "expected to match a char in 0000-FFFF; matched=%s, start=%s, end=%s, regex=%s"
                .formatted(Integer.toHexString(x), start, end, rule.regex().value());
            throw new FatalEx(start, new Exception(msg), path);
        }
        return new OK(Character.valueOf((char)x.intValue()), g0End, null);
    }
    static OK tryGetSingleCodePoint(RegexRule rule, CharSequence chars,
                                   int start, int end, int g0End, Path path) throws FatalEx
    {
        int count = Character.codePointCount(chars, start, end);
        if(count==1)
        {
            int code = Character.codePointAt(chars, start);
            if(Character.charCount(code)==end-start) // could it fail?
                return new OK(code, g0End, null);
        }
        // programming mistake in the regex, or an unexpected input
        String msg = "expected to match exactly 1 code point; start=%s, end=%s, regex=%s"
            .formatted(start, end, rule.regex().value());
        throw new FatalEx(start, new Exception(msg), path);
    }
}
