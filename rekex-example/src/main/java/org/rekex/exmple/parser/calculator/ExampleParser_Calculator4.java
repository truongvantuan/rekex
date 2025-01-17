package org.rekex.exmple.parser.calculator;

import org.rekex.annotype.AnnoType;
import org.rekex.annotype.PrimitiveType;
import org.rekex.common_util.AnnoBuilder;
import org.rekex.exmple.parser.ExampleParserUtil;
import org.rekex.helper.anno.Ch;
import org.rekex.parser.PegParser;
import org.rekex.parser.PegParserBuilder;
import org.rekex.spec.Ctor;
import org.rekex.spec.Regex;

import java.lang.annotation.*;
import java.util.Set;
import java.util.function.Function;

// instead of wrapper types like `record Term(int)`,
// use annotated primitive types like `@Term int`
public interface ExampleParser_Calculator4
{
    interface RulesCatalog
    {
        // to annotate primitive types, creating distinct annotated types.
        // instead creating @Expr etc, just use @N("expr")

        @Target(ElementType.TYPE_USE)@Retention(RetentionPolicy.RUNTIME)
        @interface N{ String value(); }

        // It is going to be painfully obvious that N.value always agrees
        // with the parameter name or the method name, thus redundant.
        // We could derive a grammar from identifiers only, e.g.
        //     double term(double factor);  double factor(...);
        // which represents rules:  term->factor, factor->...
        // That is beautiful; could be another useful project.
        //
        // However, we need @N, in this example, only because we use 1 primitive type
        // to represent different things. Most grammars probably require
        // distinct Types for different things, thus don't need annotations anyway.
        //
        // the point of this example, besides triggering brain freezes,
        // is to examine the *possibility* of it; not to encourage it.

        // rules
        //
        // expr  -> term termR
        // termR -> +- term termR | ""
        // term  -> fact factR
        // factR -> */ fact factR | ""
        // fact  -> ( expr ) | digits
        // digits-> digit digitR
        // digitR-> digit digitR  | ""
        // digit -> 0-9


        @Ctor public static
        @N("expr")double expr(@N("term")double term, @N("termR")double termR)
        {
            return termR+term;
        }

        @Ctor public static
        @N("termR")double termR_1(@Ch("+-")char op, @N("term")double term, @N("termR")double termR)
        {
            return op=='+' ? termR+term : termR-term; // // A-B+C=C-B+A
        }
        @Ctor public static
        @N("termR")double termR_2()
        {
            return 0;
        }

        @Ctor public static
        @N("term")double term(@N("fact")double fact, @N("factR")double factR)
        {
            return factR*fact;
        }


        @Ctor public static
        @N("factR")double factR_1(@Ch("*/")char op, @N("fact")double fact, @N("factR")double factR)
        {
            return op=='*' ? factR*fact : factR/fact; // A/B*C=C/B*A
            // that doesn't work for integer divisions. (not precise for doubles either)
        }
        @Ctor public static
        @N("factR")double factR_2()
        {
            return 1;
        }

        @Ctor public static
        @N("fact")double fact_1(@Ch("(")char lp, @N("expr")double expr, @Ch(")")char rp)
        {
            return expr;
        }

        @Ctor public static
        @N("fact")double fact_2(@N("digits")Digits digits)
        {
            return digits.value;
        }

        // this can be replaced by `long`, so that we create zero Objects during parsing.
        // keep this class for now to see more clearly how digits are concatenated.
        record Digits(int count, int value){}

        @Ctor public static
        @N("digits")Digits digits(@N("digit")int digit, @N("digitR")Digits digitR)
        {
            return concat(digit, digitR);
        }

        @Ctor public static
        @N("digitR")Digits digitR_1(@N("digit")int digit, @N("digitR")Digits digitR)
        {
            // this is the same as "digits"; but we saved one indirection.
            return concat(digit, digitR);
        }
        @Ctor public static
        @N("digitR")Digits digitR_2()
        {
            return new Digits(0,0);
        }

        @Ctor public static
        @N("digit")int digit(@Regex("[0-9]")char c)
        {
            return c-'0';
        }

        private static Digits concat(int digit, Digits digitR)
        {
            long value = digit;
            for(int i = 0; i<digitR.count; i++)
                value *= 10;
            value += digitR.value;
            if(value>Integer.MAX_VALUE)
                throw new IllegalArgumentException("number too big");
            return new Digits(digitR.count +1, (int)value);
        }

    }


    // test -----------------------------------------------------

    public static PegParser<Double> parser()
    {
        // @N("expr")double
        Annotation n_expr = AnnoBuilder.build(RulesCatalog.N.class, "expr");
        AnnoType type = new PrimitiveType(Set.of(n_expr), double.class);

        return new PegParserBuilder()
            .rootType(type)
            .ctorCatalog(RulesCatalog.class)
            .parser();
    }
    public static Function<Double, Double> eval()
    {
        return d->d;
    }

    public static void main(String[] args) throws Exception
    {
        ExampleParserUtil.testInputs("Calculator4", parser(), eval());
    }

}
