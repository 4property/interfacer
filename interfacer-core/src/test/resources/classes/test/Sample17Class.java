package test;

public class Sample17Class {
    sealed interface Shape permits Triangle, Rectangle {
        double calculateArea();
    }

    record Triangle(double base, double height) implements Shape {
        @Override
        public double calculateArea() {
            return base * height / 2;
        }
    }

    record Rectangle(double width, double height) implements Shape {
        @Override
        public double calculateArea() {
            return width * height;
        }
    }

    public String getName() {
        return "Sample17Class";
    }
    

    void testStringJava17(String s) {
        switch (s) {
            case null -> System.out.println("Unknown!");
            case "Java 11", "Java 17", "Java 21" -> System.out.println("LTS");
            default -> System.out.println("Ok");
        }
    }

    public static void main(String[] args) {
        Sample17Class sample = new Sample17Class();
        sample.testShape(new Triangle(10, 20));
        sample.testShape(new Rectangle(10, 20));
        sample.testStringJava17("Java 16"); // Ok
        sample.testStringJava17("Java 11"); // LTS
        sample.testStringJava17(""); // Ok
        sample.testStringJava17(null);
    }

}
