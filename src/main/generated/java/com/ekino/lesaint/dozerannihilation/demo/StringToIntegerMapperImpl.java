package com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Nullable;

/**
 * StringToIntegerMapperImpl -
 *
 * @author lesaint
 */
class StringToIntegerMapperImpl implements StringToIntegerMapper {
    private final StringToInteger stringToInteger;

    protected StringToIntegerMapperImpl(StringToInteger stringToInteger) {
        this.stringToInteger = stringToInteger;
    }

    @Override
    public Integer apply(@Nullable String input) {
        return stringToInteger.apply(input);
    }
}
