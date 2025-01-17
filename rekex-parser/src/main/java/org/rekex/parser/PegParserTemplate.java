package org.rekex.parser;

import java.nio.file.Paths;
import org.rekex.codegen.TemplateToMaker;
import org.rekex.spec.Peek;

//** comment: run the main method after modifying this template.

//** template fileHeader(packageName, IMPORT_LINES, className`PegParserTemplate`, typeArg`/*typeArg*/Void`,  ROOT_TYPE_STR, rootId`0000`) + + + + + + + + + +

//** line // !! this file is automatically generated; do not modify.

//** line package packageName;

//** line import org.rekex.parser.PegParser;
//** line import org.rekex.parser.ParseResult;

//** line IMPORT_LINES

// Generated recursive descent PEG parser by Rekex

// rootType: ROOT_TYPE_STR

//** line // !! this file is automatically generated; do not modify.
@SuppressWarnings({"all", "unchecked"})
//** line public
class PegParserTemplate implements PegParser</*typeArg*/Void>
{
    //** comment: the generated class has dependencies on rekex.
    static final int rootTypeRuleId = 0000;

    public PegParserTemplate()
    {
    }

    @Override
    public ParseResult</*typeArg*/Void> parse(CharSequence chars, int start, int end)
    {
        _State state = new _State();
        state.chars = chars;
        state.start = start;
        state.end = end;

        try
        {
            state = match(rootTypeRuleId, state, -1);
        }
        catch (_FatalEx fatalEx)
        {
            var stack = pathToStack(state.path, state.pathLen);
            return new ParseResult.Fatal<>(fatalEx.position, fatalEx.cause, stack);
        }

        if(!state.fail)
        {
            /*typeArg*/Void obj = state.pickObj();
            if(state.start==state.end)
                return new ParseResult.Full<>(obj);
            else
                return new ParseResult.Partial<>(obj, state.start);
        }
        else
        {
            var stack = pathToStack(state.maxFailPath, state.maxFailPath.length);
            String msg = failMsg(state.maxFailReason, state.maxFailInfo);
            return new ParseResult.Fail<>(state.maxFailPos, msg, stack);
        }
    }

    //** comment "State" may collide with user type names; underscore ours.
    final static class _State implements Cloneable
    {
        CharSequence chars;
        int start;
        int end;

        boolean fail;
        // if fail==false, match is success, obj is set (null is allowed)
        Object obj;
        //** comment: boxed/unboxed for primitive types; could be optimized for such cases

        int[] path = new int[64];
        int pathLen = 0;

        int maxFailPos = -1;
        int[] maxFailPath;
        int maxFailReason;
        Object maxFailInfo;

        public _State clone()
        {
            //** comment: mutable path is shared ! caller beware !
            //** comment: maxFailPath is readonly; ok to share.
            try {
                return (_State)super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        int gStart, gEnd; // tmp data buffer for regex

        void pathPush(int ruleId, int subIndex)
        {
            if(pathLen+3 > path.length)
                path = java.util.Arrays.copyOf(path, path.length+64);
            path[pathLen++] = ruleId;
            path[pathLen++] = this.start;
            path[pathLen++] = subIndex;
        }
        void pathPop()
        {
            pathLen -= 3;
        }

        _State ok(Object obj)
        {
            this.fail = false;
            this.obj = obj;
            return this;
        }
        <T> T pickObj()
        {
            T t = (T)obj;
            obj = null;
            return t;
        }

        _State fail(int position, int reason, Object info, int startReset)
        {
            if(position>maxFailPos)
            {
                maxFailPos = position;
                maxFailPath = java.util.Arrays.copyOf(path, pathLen);
                maxFailReason = reason;
                maxFailInfo = info;
            }
            return fail(startReset);
        }
        _State fail(int startReset)
        {
            this.fail = true;
            this.obj = null;
            this.start = startReset;
            return this;
        }

    }
    static final int failReason_illegal_arg = 0; // info: IllegalArgumentException
    static final int failReason_neg = 1;         // info: subrule ID
    static final int failReason_regex = 2;
    static final int failReason_regex_group = 3;

    static String failMsg(int reason, Object info)
    {
        return switch (reason){
            case failReason_illegal_arg
                -> "ctor throws "+info;
            case failReason_neg
                -> "Not<?> failed; input matches subrule";
            case failReason_regex
                -> "Input does not match regex";
            case failReason_regex_group
                -> "Input does not match regex group";
            default -> throw new AssertionError("unexpected reason: "+reason);
        };
    }

    static java.util.ArrayList<ParseResult.Node> pathToStack(int[] path, int pathLen)
    {
        java.util.ArrayList<ParseResult.Node> stack = new java.util.ArrayList<>(pathLen/2);
        for(int i=pathLen-1; i>=0;)
        {
            int _subIndex = path[i--];
            int _start = path[i--];
            int _ruleId = path[i--];
            var datatype = _DatatypeList.list.get(_ruleId);
            stack.add(new ParseResult.Node(datatype, _start, _subIndex));
        }
        return stack;
    }

    static class _FatalEx extends Exception
    {
        final int position;
        final Exception cause;
        _FatalEx(int position, Exception cause)
        {
            super(null, null, false, false);
            this.position = position;
            this.cause = cause;
        }
    }

    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    // rules
    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

    //** template matchAnyRuleIdHeader() + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
    static _State match(int ruleId, _State state, int subIndex) throws _FatalEx
    {
        state.pathPush(ruleId, subIndex);
        state = switch(ruleId){
            //** template matchAnyRuleIdCase(RULE_ID, ruleDesc)
            case RULE_ID -> rule_RULE_ID(state); // ruleDesc
            //** template matchAnyRuleIdFooter() + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
            default -> throw new AssertionError("unknown ruleId: "+ruleId);
        };
        state.pathPop();
        return state;
    }

    //** end - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    // for rekex dev. run main() after modifying this file. PegParserTemplate -> PegParserMaker
    public static void main(String[] args) throws Exception
    {
        String targetClassName = "PegParserMaker";

        var currPath = Paths.get("").toAbsolutePath();
        var srcRootPath = currPath.resolve("rekex-parser/src/main/java");
        String srcClassName = PegParserTemplate.class.getCanonicalName();

        System.out.println("current path: "+currPath);
        System.out.println("srcRoot path: "+srcRootPath);
        System.out.println("source class: "+srcClassName);
        System.out.println("target class: "+targetClassName);

        TemplateToMaker.transform(srcRootPath, srcClassName, targetClassName);

    }

    static Object args;
    static class TypeName
    {
        TypeName(){}
        TypeName(Object args) throws Exception{}

        static Object fieldName;

        static TypeName methodName(Object args) throws Exception{ return null; }
    }
    static class CompoType
    {}
    static class BoxedCompoType extends CompoType
    {}

    final static int RULE_ID=0;
    static _State rule_RULE_ID(_State state) throws _FatalEx
    {
        return state;
    }

    static int ruleId;
    static int subId;
    static int subIndex;
    static int altId;
    static int concatId;
    static int repeatId;
    static int peekId;
    static int negId;

    //** comment  # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #  alt

    //** template matchAltHeader(altId, datatypeStr) + + + + + + + + + + + + + + + + + + +
    // alt rule for: datatypeStr
    static _State rule_altId(_State state) throws _FatalEx
    {
        //** template matchAltSubRule(subId, subIndex, subDesc)
        state = match(subId, state, subIndex); // subDesc
        if(!state.fail)
            return state;

        //** template matchAltFooter() + + + + + + + + + + + + + + + + + + + + + + + + + + +
        //** comment: failed all alternatives; *assuming* subrules are not empty
        return state;
    }

    //** comment  # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #  concat

    //** template matchConcatHeader(concatId, datatypeStr)
    //** comment  + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
    // concat rule for: datatypeStr
    static _State rule_concatId(_State state) throws _FatalEx
    {
        final int start0 = state.start;

        //** template matchConcatSubRule(subIndex, subId, subType`TypeName`, subDesc) + + + + + + + + + + + + + +
        state = match(subId, state, subIndex); // arg_subIndex: subDesc
        if(state.fail)
            return state.fail(start0);
        TypeName arg_subIndex = state.pickObj();

        //** template instantiateHeader(TypeName) + + + + + + + + + + + + + + + + + + + +
        TypeName value;
        try{
            //** template instantiateNewInstance(TypeName, args) + + + + + + + + + + + + + + + + + + + +
            value = new TypeName(args);
            //** template instantiateStaticMethod(TypeName, methodName, args) + + + + + + + + + + + + + + + +
            value = TypeName.methodName(args);
            //** template instantiateFooter() + + + + + + + + + + + + + + + + + + + +
        }catch (IllegalArgumentException ex){
            return state.fail(state.start, failReason_illegal_arg, ex, start0);
        }catch(Exception ex){
            throw new _FatalEx(state.start, ex);
        }
        return state.ok(value);
        //** template matchConcatFooter() + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
    }

    //** end

    //** comment  # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #   repeat

    //** template generic_array() + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
    static <T> T[] generic_array(T... array)
    {
        return array;
    }

    //** template match_repeat() + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
    static _State match_repeat(_State state, int ruleId, int subRuleId, int min, int max) throws _FatalEx
    {
        final int start0 = state.start;
        java.util.ArrayList<Object> list = new java.util.ArrayList<>(Math.max(min, Math.min(10, max)));
        for(int i=0; i<max; i++)
        {
            final int start1 = state.start;
            state = match(subRuleId, state, i);
            if(i<min && state.fail)
                return state.fail(start0);
            if(i>=min && state.fail) // enough elements
                break;
            if(i>=min && start1==state.start) // no progress, enough elements
                break;
            list.add(state.pickObj());
        }
        return state.ok(list);
    }

    //** template match_repeat_list(ruleId, L1RuleId, datatypeStr, subId, CompoType, min`0000`, max`1111`)
    // repeat rule for: datatypeStr
    static _State rule_L1RuleId(_State state) throws _FatalEx
    {
        state = match_repeat(state, ruleId, subId, 0000, 1111);
        if(state.fail)
            return state;
        java.util.ArrayList<CompoType> list = state.pickObj();
        list.trimToSize();
        return state.ok(list);
    }

    //** template match_repeat_obj_array(ruleId, L2RuleId, datatypeStr, subId, CompoType, min`0000`, max`1111`)
    // repeat rule for: datatypeStr
    static _State rule_L2RuleId(_State state) throws _FatalEx
    {
        state = match_repeat(state, ruleId, subId, 0000, 1111);
        if(state.fail)
            return state;
        java.util.ArrayList<CompoType> list = state.pickObj();
        CompoType[] array = list.toArray(generic_array());
        return state.ok(array);
    }

    //** template match_repeat_prim_array(ruleId, L3RuleId, datatypeStr, subId, CompoType, min`0000`, max`1111`, BoxedCompoType)
    // repeat rule for: datatypeStr
    static _State rule_L3RuleId(_State state) throws _FatalEx
    {
        state = match_repeat(state, ruleId, subId, 0000, 1111);
        if(state.fail)
            return state;
        java.util.ArrayList<BoxedCompoType> list = state.pickObj();
        CompoType[] array = new CompoType[list.size()];
        for(int i=0; i<list.size(); i++)
            array[i] = list.get(i);
        return state.ok(array);
    }

    //** end

    //** comment  # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # peek/not

    //** template match_peek(peekId, datatypeStr, subId, CompoType)
    // peek rule for: datatypeStr
    static _State rule_peekId(_State state1) throws _FatalEx
    {
        //** comment: in case subrule succeeds, clear its fail info
        _State state2 = state1.clone();
        state2 = match(subId, state2, -1);
        if(state2.fail)
            return state2;

        CompoType value = state1.pickObj();
        return state1.ok(new Peek<>(value));
    }

    //** template match_neg(negId, datatypeStr, TypeName, subId)
    // neg rule for: datatypeStr
    static _State rule_negId(_State state1) throws _FatalEx
    {
        //** comment: fail info in subrule are discarded in any case
        _State state2 = state1.clone();
        state2 = match(subId, state2, -1);
        if(state2.fail)
            return state1.ok(new TypeName());
        return state1.fail(state1.start, failReason_neg, subId, state1.start);
    }

    //** comment  # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #   regex

    //** template match_regex() + + + + + + + + + + + + + + + + +
    static _State match_regex(_State state, int ruleId, java.util.regex.Pattern pattern, int group)
    {
        var matcher = pattern.matcher(state.chars);
        matcher.region(state.start, state.end);
        boolean matched = matcher.lookingAt();
        if(!matched)
            return state.fail(state.start, failReason_regex, null, state.start);

        state.gStart = matcher.start(group);
        state.gEnd = matcher.end(group);
        if(state.gStart==-1)
            return state.fail(state.start, failReason_regex_group, null, state.start);

        state.start = matcher.end(0); // consume group 0
        return state.ok(null);
    }
    //** template match_regex_str() + + + + + + + + + + + + + + + + +
    static _State match_regex_str(_State state, int ruleId, java.util.regex.Pattern pattern, int group)
    {
        state = match_regex(state, ruleId, pattern, group);
        if(state.fail)
            return state;
        String str = state.chars.subSequence(state.gStart, state.gEnd).toString();
        return state.ok(str);
    }
    //** template match_regex_char() + + + + + + + + + + + + + + + + +
    static _State match_regex_char(_State state, int ruleId, java.util.regex.Pattern pattern, int group) throws _FatalEx
    {
        state = match_regex_int(state, ruleId, pattern, group);
        if(state.fail)
            return state;
        Integer x = state.pickObj();
        if(x>0xFFFF)
        {
            // programming mistake in the regex, or an unexpected input
            String msg = "expected to match exactly 1 char; matched=%s, start=%s, end=%s, regex=%s"
                .formatted(Integer.toHexString(x), state.gStart, state.gEnd, pattern.pattern());
            throw new _FatalEx(state.gStart, new Exception(msg));
        }
        return state.ok(Character.valueOf((char)x.intValue()));
    }
    //** template match_regex_int() + + + + + + + + + + + + + + + + +
    static _State match_regex_int(_State state, int ruleId, java.util.regex.Pattern pattern, int group) throws _FatalEx
    {
        state = match_regex(state, ruleId, pattern, group);
        if(state.fail)
            return state;
        int count = Character.codePointCount(state.chars, state.gStart, state.gEnd);
        if(count==1)
        {
            int code = Character.codePointAt(state.chars, state.gStart);
            if(Character.charCount(code)==state.gEnd-state.gStart) // could it fail?
                return state.ok(code);
        }
        // programming mistake in the regex, or an unexpected input
        String msg = "expected to match exactly 1 code point; start=%s, end=%s, regex=%s"
            .formatted(state.gStart, state.gEnd, pattern.pattern());
        throw new _FatalEx(state.gStart, new Exception(msg));
    }
    //** template match_regex_obj() + + + + + + + + + + + + + + + + +
    static _State match_regex_obj(_State state, int ruleId, java.util.regex.Pattern pattern, int group, Object obj)
    {
        state = match_regex(state, ruleId, pattern, group);
        if(state.fail)
            return state;
        return state.ok(obj);
    }
    //** template matchRegexToStr(ruleId, R1ruleId, datatypeStr, group`0000`) + + + + + + + + + + + + + + + + + + + + + + + + + + +
    // regex rule for: datatypeStr
    static _State rule_R1ruleId(_State state)
    {
        return match_regex_str(state, ruleId, pattern_ruleId, 0000);
    }
    //** template matchRegexToChar(ruleId, R2ruleId, datatypeStr, group`0000`) + + + + + + + + + + + + + + + + + + + + + + + + + + +
    // regex rule for: datatypeStr
    static _State rule_R2ruleId(_State state) throws _FatalEx
    {
        return match_regex_char(state, ruleId, pattern_ruleId, 0000);
    }
    //** template matchRegexToInt(ruleId, R3ruleId, datatypeStr, group`0000`) + + + + + + + + + + + + + + + + + + + + + + + + + + +
    // regex rule for: datatypeStr
    static _State rule_R3ruleId(_State state) throws _FatalEx
    {
        return match_regex_int(state, ruleId, pattern_ruleId, 0000);
    }
    //** template matchRegexToField(ruleId, R4ruleId, datatypeStr, group`0000`, TypeName, fieldName) + + + + + + + + + + + + + + + + + + + + + + + + + + +
    // regex rule for: datatypeStr
    static _State rule_R4ruleId(_State state)
    {
        return match_regex_obj(state, ruleId, pattern_ruleId, 0000, TypeName.fieldName);
    }
    //** template patternField(ruleId, regex`"regex"`, flags`0000`) + + + + + + + + + + + + + + + + + + + + + + + + + + +
    static final java.util.regex.Pattern pattern_ruleId = java.util.regex.Pattern.compile("regex", 0000);
    //** comment "Pattern" is a common word that may appear in user's datatypes; don't import it.

    //** template helperMethodsHeader() + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +


    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    //
    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

    //** comment we need datatypes of rules in case of Fail
    //** template datatypeListHeader() + + + + + + + + + + + + + + + + + + + + + + + + + +
    static class _DatatypeList
    {
        //** template datatypeField(TypeName, datatypeStr, ruleId`000`) + + + + + + + + + + + + + + + +
        // [000] datatypeStr
        static TypeName t_000;
        //** template datatypeListFooter(total`0001`) + + + + + + + + + + + + + + + + + + + + + + + +

        static final java.util.ArrayList<org.rekex.annotype.AnnoType> list = new java.util.ArrayList<>();
        static
        {
            for(int id=0; id<0001; id++)
            {
                java.lang.reflect.Field field;
                try{ field = _DatatypeList.class.getDeclaredField("t_"+id); }
                catch(Exception ex){ throw new Error(ex); }
                var type = org.rekex.annotype.TypeMath.convertFromJlr(field.getAnnotatedType());
                list.add(type);
            }
        }
    }

    //** template fileFooter() + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + +
}
