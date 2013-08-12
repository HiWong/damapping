package java.com.ekino.lesaint.dozerannihilation.demo;

import com.ekino.lesaint.dozerannihilation.demo.StringToInteger;
import org.springframework.stereotype.Component;

/**
 * StringToIntegerMapperFactory -
 *
 * @author Sébastien Lesaint
 */
@Component
public class StringToIntegerMapperFactoryImpl implements StringToIntegerMapperFactory {

    @Override
    public StringToIntegerMapper bigDecimal() {
        return new StringToIntegerMapperImpl(StringToInteger.integer());
    }

    @Override
    public StringToIntegerMapper integer() {
        return new StringToIntegerMapperImpl(StringToInteger.integer());
    }

    @Override
    public StringToIntegerMapper instance(boolean bigDecimal) {
        return new StringToIntegerMapperImpl(StringToInteger.instance(bigDecimal));
    }
}
