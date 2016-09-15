# android-calccore
Calculator Core used for [Travel Calculator](https://play.google.com/store/apps/details?id=com.tomosware.currency.RealCurrencyCalc "Travel Calculator") App on Android

## Objective

android-calccore is the core calculation engine used by tomosware's App: [Travel Calculator](https://play.google.com/store/apps/details?id=com.tomosware.currency.RealCurrencyCalc "Travel Calculator") on Android.

We've found many calculator apps in the market having not so good calculation ability. Since we've done with this a little complicated work, we'd like to share with community. Hope we all could build better Apps to ease more people's life.

## Brief Features

Calc class supports simple arithmetic operations. The operations supported are: Add/Sub/Mul/Div.

The order and priority of operations are taken into account for final result. For example, input with 3 + 5 x 3, you will receive 18 after calling "evaluate".

Also Redo & Undo are supported. Just invoke related methods to get it done.

## How to install

Pull this repo to your local storage. Add Calc.java & OpType.java into your own project.

Remember to change package name to map correct directory.

And, it's done.

## How to use it in App

Create an instance of Calc. And use its methods to input operation and operands.

Something like:

```java
  // The following will do 3.0 + 5.0 = 
  Calc calc = new Calc();
  calc.number(3.0);   // Use number to input operand
  calc.doOp(OpType.OpAdd);    // invoke doOp to perform operation
  calc.number(5.0);
  Double result = calc.evaluate();    // invoke evaluate to get result by now

  // Now result = 8.0
  // Do whatever you like with result
  // ...
```

or you can input a series of operations and operands to fulfill your calculation.

```java
  // Create the instance
  Calc calc = new Calc();

  // Performs 4.0 + 6.0 x 5.0 = 
  calc.number(4.0);
  calc.doOp(OpType.OpAdd);
  calc.number(6.0);
  calc.doOp(OpType.OpMul);
  calc.number(5.0);
  Double result = calc.evaluate();
  
  // Now result = 34.0
  // Do the stuffs
```
