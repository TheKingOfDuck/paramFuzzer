# paramFuzzer

一款高效的参数fuzz工具|A faster param fuzzing test tool

二分法暴力的Fuzz参数，本地测试23000+的字典1秒内跑完。

同等条件下，效率上肯定比其他工具快就对了.

-u参数，-m参数是必须的，-p和-v分别是指定参数字典以及参数值，可有可无。

options:

```
java -jar paramFuzzer.jar -h
```

![](https://github.com/TheKingOfDuck/paramFuzzer/blob/master/img/screenshot.png)

### TODO LIST：

1. 读取数据包。任意位置FUZZ。
2. 识别中间件，再次加快FUZZ效率。
