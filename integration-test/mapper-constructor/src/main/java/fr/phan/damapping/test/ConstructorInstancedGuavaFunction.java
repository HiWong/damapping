package fr.phan.damapping.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import fr.phan.damapping.annotation.Mapper;

@Mapper
public class ConstructorInstancedGuavaFunction implements Function<Integer, String> {

    @Override
    public String apply(@Nullable Integer input) {
        return input.toString();
    }
}