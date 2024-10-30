package test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sample11Class {

    public String getName() {
        return "Sample11Class";
    }

    /*
     * var - Local variable type inference in Lambda Expressions
     */
    String localVarTypeInference() {
        List<String> list = Arrays.asList("a", "b", "c");
        String result = list.stream()
                .map((var x) -> x.toUpperCase())
                .collect(Collectors.joining(","));
        return result;
    }

    public static void main(String[] args) {
        Sample11Class sample = new Sample11Class();
        System.out.println(sample.localVarTypeInference());
    }
}
