package test;

public class Sample21Class {

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
        return "Sample21Class";
    }

    public void methodWithSwitchExpression() {
        String name = getName();
        switch (name) {
            case "Sample21Class" -> System.out.println("I am Java 21 compiliant class!");
            default -> System.out.println("Unknown name");
        }
    }

    public void methodWithDynamicVarDeclaration() {
        var name = getName();
        System.out.println(name);
    }

    public void methodWithTextBlock() {
        String text = """
                Hello, I am Java 21 compiliant class!
                """;
        System.out.println(text);
    }

    public void methodWithRecord() {
        record Person(String name, int age) {
        }
        Person person = new Person("John", 30);
        System.out.println(person.name());
    }


    public String methodWithInstancePatternMatching(Object o) {
        if (o instanceof String s) {
            return s;
        } else if (o instanceof Integer i) {
            return String.valueOf(i);
        }
        return null;
    }   

    int methodWithExhaustiveSwitch(Object o) {
        return switch (o) {
            case String s  -> s.length();
            case Integer i -> i;
            default -> 0;
        };  
    }

    public void testShape(Shape s) {
        switch (s) {
            case null -> {}
            case Triangle t when (t.calculateArea() > 100) -> System.out.println("Large triangle");
            case Triangle t -> System.out.println("Triangle");
            case Rectangle r when (r.calculateArea() > 100) -> System.out.println("Large rectangle");
            case Rectangle r -> System.out.println("Rectangle");
            default -> System.out.println("Unknown!");
        }
    }


    public void methodWithSealedClass() {
        sealed class Shape permits Circle, Rectangle {
            public String getName() {
                return "Shape";
            }
        }
        class Circle extends Shape {
            public String getName() {
                return "Circle";
            }
        }
        class Rectangle extends Shape {
            public String getName() {
                return "Rectangle";
            }
        }
        Shape shape = new Circle();
        System.out.println(shape.getName());
    }

    public static void main(String[] args) {
        Sample21Class sample = new Sample21Class();
        sample.testShape(new Triangle(10, 20));
        sample.testShape(new Rectangle(10, 20));
        System.out.println(sample.methodWithExhaustiveSwitch("Java 21"));
    }   

}
