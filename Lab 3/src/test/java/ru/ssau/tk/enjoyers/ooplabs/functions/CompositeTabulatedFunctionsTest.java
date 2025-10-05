package ru.ssau.tk.enjoyers.ooplabs.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CompositeTabulatedFunctionsTest {

    private MathFunction identity;
    private MathFunction sqr;
    private MathFunction constant;
    private ArrayTabulatedFunction arrayFunc;
    private LinkedListTabulatedFunction linkedListFunc;
    private ArrayTabulatedFunction sinArrayFunc;
    private LinkedListTabulatedFunction cosLinkedListFunc;

    @BeforeEach
    void setUp() {
        identity = new IdentityFunction();
        sqr = new SqrFunction();
        constant = new ConstantFunction(3.0);

        // Табулированная функция на основе x^2 (массив)
        double[] xValues1 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues1 = {0.0, 1.0, 4.0, 9.0, 16.0};
        arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);

        // Табулированная функция на основе x^2 (список)
        double[] xValues2 = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues2 = {0.0, 1.0, 4.0, 9.0, 16.0};
        linkedListFunc = new LinkedListTabulatedFunction(xValues2, yValues2);

        // Табулированная функция на основе sin(x) (массив)
        double[] xValues3 = {0.0, Math.PI/6, Math.PI/4, Math.PI/3, Math.PI/2};
        double[] yValues3 = {0.0, 0.5, Math.sqrt(2)/2, Math.sqrt(3)/2, 1.0};
        sinArrayFunc = new ArrayTabulatedFunction(xValues3, yValues3);

        // Табулированная функция на основе cos(x) (список)
        double[] xValues4 = {0.0, Math.PI/6, Math.PI/4, Math.PI/3, Math.PI/2};
        double[] yValues4 = {1.0, Math.sqrt(3)/2, Math.sqrt(2)/2, 0.5, 0.0};
        cosLinkedListFunc = new LinkedListTabulatedFunction(xValues4, yValues4);
    }

    @Test
    void testArrayTabulatedWithLinkedListTabulated() {
        // arrayFunc(x) -> linkedListFunc(result)
        MathFunction composite = arrayFunc.andThen(linkedListFunc);

        // Обе функции представляют x^2, поэтому composite(x) = (x^2)^2 = x^4
        assertEquals(0.0, composite.apply(0.0), 1e-12);
        assertEquals(1.0, composite.apply(1.0), 1e-12);
        assertEquals(16.0, composite.apply(2.0), 1e-12);
        assertEquals(51.0, composite.apply(3.0), 1e-12);

        // Проверка интерполяции
        double result = composite.apply(1.5);
        assertEquals(6.5, result, 1e-12);
    }

    @Test
    void testLinkedListTabulatedWithArrayTabulated() {
        // linkedListFunc(x) -> arrayFunc(result)
        MathFunction composite = linkedListFunc.andThen(arrayFunc);

        // Тот же результат: x^4
        assertEquals(0.0, composite.apply(0.0), 1e-12);
        assertEquals(1.0, composite.apply(1.0), 1e-12);
        assertEquals(16.0, composite.apply(2.0), 1e-12);
    }

    @Test
    void testDifferentTabulatedFunctionsComposition() {
        // sinArrayFunc(x) -> cosLinkedListFunc(result)
        MathFunction composite = sinArrayFunc.andThen(cosLinkedListFunc);

        // composite(x) = cos(sin(x))
        // При x = 0: sin(0)=0, cos(0)=1
        assertEquals(1.0, composite.apply(0.0), 1e-12);

        // При x = π/6: sin(π/6) = 0.5, cos(0.5) примерно = 0.877582562
        double expected = Math.cos(0.5);
        assertEquals(expected, composite.apply(Math.PI/6), 0.1);
    }

    @Test
    void testTabulatedFunctionChainWithMultipleTypes() {
        // Цепочка из разных типов табулированных функций
        MathFunction chain = arrayFunc                       // x^2
                .andThen(linkedListFunc)                     // (x^2)^2 = x^4
                .andThen(sinArrayFunc)                       // sin(x^4)
                .andThen(cosLinkedListFunc);                 // cos(sin(x^4))

        double result1 = chain.apply(0.0);  // cos(sin(0)) = cos(0) = 1
        double result2 = chain.apply(1.0);  // cos(sin(1)) примерно = cos(0.84147) примерно = 0.66637

        assertEquals(1.0, result1, 1e-12);
        assertEquals(Math.cos(Math.sin(1.0)), result2, 0.1);
    }

    @Test
    void testTabulatedWithSimpleFunction() {
        // arrayFunc(x) -> sqr(result)
        MathFunction composite1 = arrayFunc.andThen(sqr);
        // sqr(x) -> arrayFunc(result)
        MathFunction composite2 = sqr.andThen(arrayFunc);

        // composite1(x) = (x^2)^2 = x^4
        assertEquals(16.0, composite1.apply(2.0), 1e-12);
        assertEquals(81.0, composite1.apply(3.0), 1e-12);

        // composite2(x) = (x^2)^2 = x^4 (такой же результат)
        assertEquals(16.0, composite2.apply(2.0), 1e-12);
        assertEquals(51.0, composite2.apply(3.0), 1e-12);
    }

    @Test
    void testTabulatedWithIdentityFunction() {
        // arrayFunc(x) -> identity(result)
        MathFunction composite1 = arrayFunc.andThen(identity);
        // identity(x) -> arrayFunc(result)
        MathFunction composite2 = identity.andThen(arrayFunc);

        // Обе композиции должны давать x^2
        assertEquals(4.0, composite1.apply(2.0), 1e-12);
        assertEquals(4.0, composite2.apply(2.0), 1e-12);
        assertEquals(9.0, composite1.apply(3.0), 1e-12);
        assertEquals(9.0, composite2.apply(3.0), 1e-12);

        // Проверка интерполяции
        assertEquals(2.5, composite1.apply(1.5), 1e-12);
        assertEquals(2.5, composite2.apply(1.5), 1e-12);
    }

    @Test
    void testTabulatedWithConstantFunction() {
        // arrayFunc(x) -> constant(result)
        MathFunction composite1 = arrayFunc.andThen(constant);
        // constant(x) -> arrayFunc(result)
        MathFunction composite2 = constant.andThen(arrayFunc);

        // composite1(x) = 3 (постоянно)
        assertEquals(3.0, composite1.apply(0.0), 1e-12);
        assertEquals(3.0, composite1.apply(10.0), 1e-12);
        assertEquals(3.0, composite1.apply(-5.0), 1e-12);

        // composite2(x) = arrayFunc(3) = 9
        assertEquals(9.0, composite2.apply(0.0), 1e-12);
        assertEquals(9.0, composite2.apply(100.0), 1e-12);
    }

    @Test
    void testComplexMixedComposition() {
        // Сложная цепочка: identity -> arrayFunc -> sqr -> linkedListFunc -> constant
        MathFunction complexChain = identity
                .andThen(arrayFunc)          // x^2
                .andThen(sqr)                // (x^2)^2 = x^4
                .andThen(linkedListFunc)     // (x^4)^2 = x^8
                .andThen(constant);          // всегда 3

        // Независимо от x, результат всегда 3
        assertEquals(3.0, complexChain.apply(0.0), 1e-12);
        assertEquals(3.0, complexChain.apply(1.0), 1e-12);
        assertEquals(3.0, complexChain.apply(2.0), 1e-12);
        assertEquals(3.0, complexChain.apply(10.0), 1e-12);
    }

    @Test
    void testCompositionWithModifiedTabulatedFunctions() {
        // Создаем функцию и модифицируем её
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction modifiedArrayFunc = new ArrayTabulatedFunction(xValues, yValues);

        // Модифицируем значения
        modifiedArrayFunc.setY(1, 10.0); // Теперь при x=1, y=10

        // Композиция с другой функцией
        MathFunction composite = modifiedArrayFunc.andThen(sqr);

        assertEquals(0.0, composite.apply(0.0), 1e-12);   // 0^2 = 0
        assertEquals(100.0, composite.apply(1.0), 1e-12); // 10^2 = 100
        assertEquals(16.0, composite.apply(2.0), 1e-12);  // 4^2 = 16

        // Проверка интерполяции с модифицированными значениями
        double interpolated = composite.apply(0.5); // между 0 и 10
        assertTrue(interpolated > 0 && interpolated < 100);
    }

    @Test
    void testLargeTabulatedFunctionComposition() {
        // Создаем большую табулированную функцию
        int size = 1000;
        double[] xValues = new double[size];
        double[] yValues = new double[size];

        for (int i = 0; i < size; i++) {
            xValues[i] = i;
            yValues[i] = i * i;
        }

        ArrayTabulatedFunction largeArrayFunc = new ArrayTabulatedFunction(xValues, yValues);
        LinkedListTabulatedFunction largeLinkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);

        // Композиция больших функций
        MathFunction composite = largeArrayFunc.andThen(largeLinkedListFunc);

        // Проверяем несколько точек
        assertEquals(0.0, composite.apply(0.0), 1e-12);
        assertEquals(1.0, composite.apply(1.0), 1e-12);
        assertEquals(16.0, composite.apply(2.0), 1e-12);

        // Проверяем интерполяцию в середине
        double result = composite.apply(1.5);
        assertTrue(result > 1.0 && result < 16.0);
    }

    @Test
    void testEdgeCasesWithTabulatedComposition() {
        // Тестируем граничные случаи

        // Функция с одной точкой
        double[] singleX = {5.0};
        double[] singleY = {25.0};
        ArrayTabulatedFunction singlePointFunc = new ArrayTabulatedFunction(singleX, singleY);

        MathFunction composite = singlePointFunc.andThen(sqr);

        // Для любой точки x, singlePointFunc возвращает 25, затем sqr(25) = 625
        assertEquals(625.0, composite.apply(0.0), 1e-12);
        assertEquals(625.0, composite.apply(10.0), 1e-12);
        assertEquals(625.0, composite.apply(-5.0), 1e-12);
    }

    @Test
    void testCompositionWithDifferentDomains() {
        // Функции с разными областями определения
        double[] xValues1 = {0.0, 1.0, 2.0};          // [0, 2]
        double[] yValues1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {-2.0, -1.0, 0.0, 1.0, 2.0}; // [-2, 2]
        double[] yValues2 = {4.0, 1.0, 0.0, 1.0, 4.0};   // |x|
        LinkedListTabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        // func1(x) -> func2(result)
        MathFunction composite = func1.andThen(func2);

        // При x=0: func1(0)=0, func2(0)=0
        assertEquals(0.0, composite.apply(0.0), 1e-12);

        // При x=1: func1(1)=1, func2(1)=1
        assertEquals(1.0, composite.apply(1.0), 1e-12);

        // При x=2: func1(2)=4, func2(4) - экстраполяция справа
        double result = composite.apply(2.0);
        assertTrue(result > 4.0); // Экстраполяция для func2 при x=4
    }

    @Test
    void testAndThenChainingWithMixedFunctions() {
        // Демонстрация удобства использования andThen для создания сложных цепочек
        MathFunction complexFunction = identity
                .andThen(x -> x + 1)             // x + 1
                .andThen(arrayFunc)                     // (x + 1)^2
                .andThen(linkedListFunc)                // ((x + 1)^2)^2 = (x + 1)^4
                .andThen(sinArrayFunc)                  // sin((x + 1)^4)
                .andThen(Math::cos)                     // cos(sin((x + 1)^4))
                .andThen(constant.andThen(sqr));        // 3^2 = 9 (постоянно)

        // Все вычисления сводятся к константе 9
        assertEquals(9.0, complexFunction.apply(0.0), 1e-12);
        assertEquals(9.0, complexFunction.apply(1.0), 1e-12);
        assertEquals(9.0, complexFunction.apply(100.0), 1e-12);
    }
}
