package org.rekex.parser;

import java.io.IOException;
import java.util.ArrayList;

// this file is generated from org/rekex/parser/PegParserTemplate.java; do not modify.
class PegParserMaker
{
    final ArrayList<Object> __parts__ = new ArrayList<>();
    private record SubSeq(CharSequence chars, int start, int end){}

    public void add(Object object)
    {
        __parts__.add(object);
    }

    public void add(CharSequence chars, int start, int end)
    {
        add(new SubSeq(chars, start, end));
    }

    public void writeTo(Appendable out) throws IOException
    {
        for(var obj : __parts__)
        {
            if(obj instanceof SubSeq ss)
                out.append(ss.chars, ss.start, ss.end);
            else
                out.append(String.valueOf(obj));
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void fileHeader(Object packageName, Object IMPORT_LINES, Object className, Object typeArg, Object ROOT_TYPE_STR, Object rootId)
    {
        add(this._fileHeader, 0, 69);
        add(packageName);
        add(this._fileHeader, 82, 158);
        add(IMPORT_LINES);
        add(this._fileHeader, 172, 239);
        add(ROOT_TYPE_STR);
        add(this._fileHeader, 254, 368);
        add(className);
        add(this._fileHeader, 379, 401);
        add(typeArg);
        add(this._fileHeader, 410, 452);
        add(rootId);
        add(this._fileHeader, 460, 474);
        add(className);
        add(this._fileHeader, 485, 538);
        add(typeArg);
        add(this._fileHeader, 547, 1056);
        add(typeArg);
        add(this._fileHeader, 1065, 5248);
    }
    final String _fileHeader = """

// !! this file is automatically generated; do not modify.

package {packageName};

import org.rekex.parser.PegParser;
import org.rekex.parser.ParseResult;

{IMPORT_LINES}

// Generated recursive descent PEG parser by Rekex

// rootType: {ROOT_TYPE_STR}

// !! this file is automatically generated; do not modify.
@SuppressWarnings({"all", "unchecked"})
public
class {className} implements PegParser<{typeArg}>
{
    static final int rootTypeRuleId = {rootId};

    public {className}()
    {
    }

    @Override
    public ParseResult<{typeArg}> parse(CharSequence chars, int start, int end)
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
            {typeArg} obj = state.pickObj();
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

    final static class _State implements Cloneable
    {
        CharSequence chars;
        int start;
        int end;

        boolean fail;
        // if fail==false, match is success, obj is set (null is allowed)
        Object obj;

        int[] path = new int[64];
        int pathLen = 0;

        int maxFailPos = -1;
        int[] maxFailPath;
        int maxFailReason;
        Object maxFailInfo;

        public _State clone()
        {
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

""";



    public void matchAnyRuleIdHeader()
    {
        add(this._matchAnyRuleIdHeader, 0, 160);
    }
    final String _matchAnyRuleIdHeader = """
    static _State match(int ruleId, _State state, int subIndex) throws _FatalEx
    {
        state.pathPush(ruleId, subIndex);
        state = switch(ruleId){
""";



    public void matchAnyRuleIdCase(Object RULE_ID, Object ruleDesc)
    {
        add(this._matchAnyRuleIdCase, 0, 17);
        add(RULE_ID);
        add(this._matchAnyRuleIdCase, 26, 35);
        add(RULE_ID);
        add(this._matchAnyRuleIdCase, 44, 56);
        add(ruleDesc);
        add(this._matchAnyRuleIdCase, 66, 67);
    }
    final String _matchAnyRuleIdCase = """
            case {RULE_ID} -> rule_{RULE_ID}(state); // {ruleDesc}
""";



    public void matchAnyRuleIdFooter()
    {
        add(this._matchAnyRuleIdFooter, 0, 141);
    }
    final String _matchAnyRuleIdFooter = """
            default -> throw new AssertionError("unknown ruleId: "+ruleId);
        };
        state.pathPop();
        return state;
    }

""";



    public void matchAltHeader(Object altId, Object datatypeStr)
    {
        add(this._matchAltHeader, 0, 21);
        add(datatypeStr);
        add(this._matchAltHeader, 34, 58);
        add(altId);
        add(this._matchAltHeader, 65, 102);
    }
    final String _matchAltHeader = """
    // alt rule for: {datatypeStr}
    static _State rule_{altId}(_State state) throws _FatalEx
    {
""";



    public void matchAltSubRule(Object subId, Object subIndex, Object subDesc)
    {
        add(this._matchAltSubRule, 0, 22);
        add(subId);
        add(this._matchAltSubRule, 29, 38);
        add(subIndex);
        add(this._matchAltSubRule, 48, 54);
        add(subDesc);
        add(this._matchAltSubRule, 63, 115);
    }
    final String _matchAltSubRule = """
        state = match({subId}, state, {subIndex}); // {subDesc}
        if(!state.fail)
            return state;

""";



    public void matchAltFooter()
    {
        add(this._matchAltFooter, 0, 30);
    }
    final String _matchAltFooter = """
        return state;
    }


""";



    public void matchConcatHeader(Object concatId, Object datatypeStr)
    {
        add(this._matchConcatHeader, 0, 24);
        add(datatypeStr);
        add(this._matchConcatHeader, 37, 61);
        add(concatId);
        add(this._matchConcatHeader, 71, 149);
    }
    final String _matchConcatHeader = """
    // concat rule for: {datatypeStr}
    static _State rule_{concatId}(_State state) throws _FatalEx
    {
        final int start0 = state.start;

""";



    public void matchConcatSubRule(Object subIndex, Object subId, Object subType, Object subDesc)
    {
        add(this._matchConcatSubRule, 0, 22);
        add(subId);
        add(this._matchConcatSubRule, 29, 38);
        add(subIndex);
        add(this._matchConcatSubRule, 48, 58);
        add(subIndex);
        add(this._matchConcatSubRule, 68, 70);
        add(subDesc);
        add(this._matchConcatSubRule, 79, 150);
        add(subType);
        add(this._matchConcatSubRule, 159, 164);
        add(subIndex);
        add(this._matchConcatSubRule, 174, 195);
    }
    final String _matchConcatSubRule = """
        state = match({subId}, state, {subIndex}); // arg_{subIndex}: {subDesc}
        if(state.fail)
            return state.fail(start0);
        {subType} arg_{subIndex} = state.pickObj();

""";



    public void instantiateHeader(Object TypeName)
    {
        add(this._instantiateHeader, 0, 8);
        add(TypeName);
        add(this._instantiateHeader, 18, 39);
    }
    final String _instantiateHeader = """
        {TypeName} value;
        try{
""";



    public void instantiateNewInstance(Object TypeName, Object args)
    {
        add(this._instantiateNewInstance, 0, 24);
        add(TypeName);
        add(this._instantiateNewInstance, 34, 35);
        add(args);
        add(this._instantiateNewInstance, 41, 44);
    }
    final String _instantiateNewInstance = """
            value = new {TypeName}({args});
""";



    public void instantiateStaticMethod(Object TypeName, Object methodName, Object args)
    {
        add(this._instantiateStaticMethod, 0, 20);
        add(TypeName);
        add(this._instantiateStaticMethod, 30, 31);
        add(methodName);
        add(this._instantiateStaticMethod, 43, 44);
        add(args);
        add(this._instantiateStaticMethod, 50, 53);
    }
    final String _instantiateStaticMethod = """
            value = {TypeName}.{methodName}({args});
""";



    public void instantiateFooter()
    {
        add(this._instantiateFooter, 0, 247);
    }
    final String _instantiateFooter = """
        }catch (IllegalArgumentException ex){
            return state.fail(state.start, failReason_illegal_arg, ex, start0);
        }catch(Exception ex){
            throw new _FatalEx(state.start, ex);
        }
        return state.ok(value);
""";



    public void matchConcatFooter()
    {
        add(this._matchConcatFooter, 0, 7);
    }
    final String _matchConcatFooter = """
    }

""";



    public void generic_array()
    {
        add(this._generic_array, 0, 80);
    }
    final String _generic_array = """
    static <T> T[] generic_array(T... array)
    {
        return array;
    }

""";



    public void match_repeat()
    {
        add(this._match_repeat, 0, 737);
    }
    final String _match_repeat = """
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

""";



    public void match_repeat_list(Object ruleId, Object L1RuleId, Object datatypeStr, Object subId, Object CompoType, Object min, Object max)
    {
        add(this._match_repeat_list, 0, 24);
        add(datatypeStr);
        add(this._match_repeat_list, 37, 61);
        add(L1RuleId);
        add(this._match_repeat_list, 71, 144);
        add(ruleId);
        add(this._match_repeat_list, 152, 154);
        add(subId);
        add(this._match_repeat_list, 161, 163);
        add(min);
        add(this._match_repeat_list, 168, 170);
        add(max);
        add(this._match_repeat_list, 175, 255);
        add(CompoType);
        add(this._match_repeat_list, 266, 357);
    }
    final String _match_repeat_list = """
    // repeat rule for: {datatypeStr}
    static _State rule_{L1RuleId}(_State state) throws _FatalEx
    {
        state = match_repeat(state, {ruleId}, {subId}, {min}, {max});
        if(state.fail)
            return state;
        java.util.ArrayList<{CompoType}> list = state.pickObj();
        list.trimToSize();
        return state.ok(list);
    }

""";



    public void match_repeat_obj_array(Object ruleId, Object L2RuleId, Object datatypeStr, Object subId, Object CompoType, Object min, Object max)
    {
        add(this._match_repeat_obj_array, 0, 24);
        add(datatypeStr);
        add(this._match_repeat_obj_array, 37, 61);
        add(L2RuleId);
        add(this._match_repeat_obj_array, 71, 144);
        add(ruleId);
        add(this._match_repeat_obj_array, 152, 154);
        add(subId);
        add(this._match_repeat_obj_array, 161, 163);
        add(min);
        add(this._match_repeat_obj_array, 168, 170);
        add(max);
        add(this._match_repeat_obj_array, 175, 255);
        add(CompoType);
        add(this._match_repeat_obj_array, 266, 300);
        add(CompoType);
        add(this._match_repeat_obj_array, 311, 392);
    }
    final String _match_repeat_obj_array = """
    // repeat rule for: {datatypeStr}
    static _State rule_{L2RuleId}(_State state) throws _FatalEx
    {
        state = match_repeat(state, {ruleId}, {subId}, {min}, {max});
        if(state.fail)
            return state;
        java.util.ArrayList<{CompoType}> list = state.pickObj();
        {CompoType}[] array = list.toArray(generic_array());
        return state.ok(array);
    }

""";



    public void match_repeat_prim_array(Object ruleId, Object L3RuleId, Object datatypeStr, Object subId, Object CompoType, Object min, Object max, Object BoxedCompoType)
    {
        add(this._match_repeat_prim_array, 0, 24);
        add(datatypeStr);
        add(this._match_repeat_prim_array, 37, 61);
        add(L3RuleId);
        add(this._match_repeat_prim_array, 71, 144);
        add(ruleId);
        add(this._match_repeat_prim_array, 152, 154);
        add(subId);
        add(this._match_repeat_prim_array, 161, 163);
        add(min);
        add(this._match_repeat_prim_array, 168, 170);
        add(max);
        add(this._match_repeat_prim_array, 175, 255);
        add(BoxedCompoType);
        add(this._match_repeat_prim_array, 271, 305);
        add(CompoType);
        add(this._match_repeat_prim_array, 316, 331);
        add(CompoType);
        add(this._match_repeat_prim_array, 342, 473);
    }
    final String _match_repeat_prim_array = """
    // repeat rule for: {datatypeStr}
    static _State rule_{L3RuleId}(_State state) throws _FatalEx
    {
        state = match_repeat(state, {ruleId}, {subId}, {min}, {max});
        if(state.fail)
            return state;
        java.util.ArrayList<{BoxedCompoType}> list = state.pickObj();
        {CompoType}[] array = new {CompoType}[list.size()];
        for(int i=0; i<list.size(); i++)
            array[i] = list.get(i);
        return state.ok(array);
    }

""";



    public void match_peek(Object peekId, Object datatypeStr, Object subId, Object CompoType)
    {
        add(this._match_peek, 0, 22);
        add(datatypeStr);
        add(this._match_peek, 35, 59);
        add(peekId);
        add(this._match_peek, 67, 168);
        add(subId);
        add(this._match_peek, 175, 250);
        add(CompoType);
        add(this._match_peek, 261, 340);
    }
    final String _match_peek = """
    // peek rule for: {datatypeStr}
    static _State rule_{peekId}(_State state1) throws _FatalEx
    {
        _State state2 = state1.clone();
        state2 = match({subId}, state2, -1);
        if(state2.fail)
            return state2;

        {CompoType} value = state1.pickObj();
        return state1.ok(new Peek<>(value));
    }

""";



    public void match_neg(Object negId, Object datatypeStr, Object TypeName, Object subId)
    {
        add(this._match_neg, 0, 21);
        add(datatypeStr);
        add(this._match_neg, 34, 58);
        add(negId);
        add(this._match_neg, 65, 166);
        add(subId);
        add(this._match_neg, 173, 245);
        add(TypeName);
        add(this._match_neg, 255, 317);
        add(subId);
        add(this._match_neg, 324, 349);
    }
    final String _match_neg = """
    // neg rule for: {datatypeStr}
    static _State rule_{negId}(_State state1) throws _FatalEx
    {
        _State state2 = state1.clone();
        state2 = match({subId}, state2, -1);
        if(state2.fail)
            return state1.ok(new {TypeName}());
        return state1.fail(state1.start, failReason_neg, {subId}, state1.start);
    }


""";



    public void match_regex()
    {
        add(this._match_regex, 0, 653);
    }
    final String _match_regex = """
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
""";



    public void match_regex_str()
    {
        add(this._match_regex_str, 0, 338);
    }
    final String _match_regex_str = """
    static _State match_regex_str(_State state, int ruleId, java.util.regex.Pattern pattern, int group)
    {
        state = match_regex(state, ruleId, pattern, group);
        if(state.fail)
            return state;
        String str = state.chars.subSequence(state.gStart, state.gEnd).toString();
        return state.ok(str);
    }
""";



    public void match_regex_char()
    {
        add(this._match_regex_char, 0, 723);
    }
    final String _match_regex_char = """
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
""";



    public void match_regex_int()
    {
        add(this._match_regex_int, 0, 853);
    }
    final String _match_regex_int = """
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
""";



    public void match_regex_obj()
    {
        add(this._match_regex_obj, 0, 267);
    }
    final String _match_regex_obj = """
    static _State match_regex_obj(_State state, int ruleId, java.util.regex.Pattern pattern, int group, Object obj)
    {
        state = match_regex(state, ruleId, pattern, group);
        if(state.fail)
            return state;
        return state.ok(obj);
    }
""";



    public void matchRegexToStr(Object ruleId, Object R1ruleId, Object datatypeStr, Object group)
    {
        add(this._matchRegexToStr, 0, 23);
        add(datatypeStr);
        add(this._matchRegexToStr, 36, 60);
        add(R1ruleId);
        add(this._matchRegexToStr, 70, 129);
        add(ruleId);
        add(this._matchRegexToStr, 137, 147);
        add(ruleId);
        add(this._matchRegexToStr, 155, 157);
        add(group);
        add(this._matchRegexToStr, 164, 173);
    }
    final String _matchRegexToStr = """
    // regex rule for: {datatypeStr}
    static _State rule_{R1ruleId}(_State state)
    {
        return match_regex_str(state, {ruleId}, pattern_{ruleId}, {group});
    }
""";



    public void matchRegexToChar(Object ruleId, Object R2ruleId, Object datatypeStr, Object group)
    {
        add(this._matchRegexToChar, 0, 23);
        add(datatypeStr);
        add(this._matchRegexToChar, 36, 60);
        add(R2ruleId);
        add(this._matchRegexToChar, 70, 146);
        add(ruleId);
        add(this._matchRegexToChar, 154, 164);
        add(ruleId);
        add(this._matchRegexToChar, 172, 174);
        add(group);
        add(this._matchRegexToChar, 181, 190);
    }
    final String _matchRegexToChar = """
    // regex rule for: {datatypeStr}
    static _State rule_{R2ruleId}(_State state) throws _FatalEx
    {
        return match_regex_char(state, {ruleId}, pattern_{ruleId}, {group});
    }
""";



    public void matchRegexToInt(Object ruleId, Object R3ruleId, Object datatypeStr, Object group)
    {
        add(this._matchRegexToInt, 0, 23);
        add(datatypeStr);
        add(this._matchRegexToInt, 36, 60);
        add(R3ruleId);
        add(this._matchRegexToInt, 70, 145);
        add(ruleId);
        add(this._matchRegexToInt, 153, 163);
        add(ruleId);
        add(this._matchRegexToInt, 171, 173);
        add(group);
        add(this._matchRegexToInt, 180, 189);
    }
    final String _matchRegexToInt = """
    // regex rule for: {datatypeStr}
    static _State rule_{R3ruleId}(_State state) throws _FatalEx
    {
        return match_regex_int(state, {ruleId}, pattern_{ruleId}, {group});
    }
""";



    public void matchRegexToField(Object ruleId, Object R4ruleId, Object datatypeStr, Object group, Object TypeName, Object fieldName)
    {
        add(this._matchRegexToField, 0, 23);
        add(datatypeStr);
        add(this._matchRegexToField, 36, 60);
        add(R4ruleId);
        add(this._matchRegexToField, 70, 129);
        add(ruleId);
        add(this._matchRegexToField, 137, 147);
        add(ruleId);
        add(this._matchRegexToField, 155, 157);
        add(group);
        add(this._matchRegexToField, 164, 166);
        add(TypeName);
        add(this._matchRegexToField, 176, 177);
        add(fieldName);
        add(this._matchRegexToField, 188, 197);
    }
    final String _matchRegexToField = """
    // regex rule for: {datatypeStr}
    static _State rule_{R4ruleId}(_State state)
    {
        return match_regex_obj(state, {ruleId}, pattern_{ruleId}, {group}, {TypeName}.{fieldName});
    }
""";



    public void patternField(Object ruleId, Object regex, Object flags)
    {
        add(this._patternField, 0, 49);
        add(ruleId);
        add(this._patternField, 57, 92);
        add(regex);
        add(this._patternField, 99, 101);
        add(flags);
        add(this._patternField, 108, 112);
    }
    final String _patternField = """
    static final java.util.regex.Pattern pattern_{ruleId} = java.util.regex.Pattern.compile({regex}, {flags});

""";



    public void helperMethodsHeader()
    {
        add(this._helperMethodsHeader, 0, 228);
    }
    final String _helperMethodsHeader = """


    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    //
    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =

""";



    public void datatypeListHeader()
    {
        add(this._datatypeListHeader, 0, 37);
    }
    final String _datatypeListHeader = """
    static class _DatatypeList
    {
""";



    public void datatypeField(Object TypeName, Object datatypeStr, Object ruleId)
    {
        add(this._datatypeField, 0, 12);
        add(ruleId);
        add(this._datatypeField, 20, 22);
        add(datatypeStr);
        add(this._datatypeField, 35, 51);
        add(TypeName);
        add(this._datatypeField, 61, 64);
        add(ruleId);
        add(this._datatypeField, 72, 74);
    }
    final String _datatypeField = """
        // [{ruleId}] {datatypeStr}
        static {TypeName} t_{ruleId};
""";



    public void datatypeListFooter(Object total)
    {
        add(this._datatypeListFooter, 0, 161);
        add(total);
        add(this._datatypeListFooter, 168, 535);
    }
    final String _datatypeListFooter = """

        static final java.util.ArrayList<org.rekex.annotype.AnnoType> list = new java.util.ArrayList<>();
        static
        {
            for(int id=0; id<{total}; id++)
            {
                java.lang.reflect.Field field;
                try{ field = _DatatypeList.class.getDeclaredField("t_"+id); }
                catch(Exception ex){ throw new Error(ex); }
                var type = org.rekex.annotype.TypeMath.convertFromJlr(field.getAnnotatedType());
                list.add(type);
            }
        }
    }

""";



    public void fileFooter()
    {
        add(this._fileFooter, 0, 2);
    }
    final String _fileFooter = """
}
""";



}