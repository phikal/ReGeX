## ReGeX

ReGeX /ˈrɛːɡɛx/ is an android game, with a simple objective: Find a 
[regular expression](https://en.wikipedia.org/wiki/Regular_expression)
which does match certain strings, but doesn't match others. We are using 
Perl's syntax. Here's an example of what the game could challenge you with:

> **Match:** `sy`, `sotx`, `f`

> **Don't Match:** `jne`, `caa`, `rua`

This could be solved by one of the following expressions:

> `sy|sotx|f`, `[sf].*`, `[^ja]*`, `.*[yxf]`

Depending on how long the expression is, how many regex characters you used
and a few more variables, your score is generated. From the number of games 
and your score (high score and less games), the current level is calculated.
The higher the level, the more complex the challenges become.

---

Version 1,1 - Licenced under: GPLv2+
