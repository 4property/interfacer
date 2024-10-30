package test;

import java.util.Optional;

public class Sample18Class {

    public String getName() {
        return "Sample18Class";
    }

    /**
     * The following code shows how to use {@code Optional.isPresent}:
     * {@snippet :
     * Optional<String> optional = Optional.of("Java 18");
     * if (optional.isPresent()) {
     *     System.out.println(optional.get());
     *  }
     * }
     */
    public void sampleOptional() {
        Optional<String> optional = Optional.of("Java 18");
        if (optional.isPresent()) {
            System.out.println(optional.get());
        }
    }

    public void methodWithTextBlock() {
        String text = """
                Hello, I am Java 18 compiliant class!
                """;
        System.out.println(text);
    }

    public void methodWithRecord() {
        record Person(String name, int age) {
        }
        Person person = new Person("John", 30);
        System.out.println(person.name());
    }

    public static void main(String[] args) {
        Sample18Class sample = new Sample18Class();
        sample.sampleOptional();
        sample.methodWithTextBlock();
        sample.methodWithRecord();
    }

}
