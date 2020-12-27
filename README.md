# 都2021年了，你的android app还在发生crash吗?

要想不crash，只能让线程不要抛出exception，唯此别无他法。

如果我们能把一个线程的所有的操作都使用try-catch进行保护，理论上，就能做到app never crash。

由于android基于Handler事件驱动的机制，可以在app启动时，向主线程中的MessageQueue中提交一个死循环操作。

在这个死循环中不断去poll事件，并且将这个死循环进行try-catch，这样所有主线程中的异常都会被catch住，从而app就再也不会发生crash。

另外，当catch住异常后，为了保证页面不乱套，关闭栈顶activity。

