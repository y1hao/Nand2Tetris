
The compiled VM files are included in the "VM" folder (load this folder to the emulator),
and the source code files are contained in the "source" folder.

This is a RPG game inspired by a classic Chinese game (http://www.4399.com/flash/1749.htm) 
which I used to play in primary school.

The settings in my game may be a bit challenging, 
and it may take several tries to get through. 
Or, in case you cannot find the solution,
you can watch me solving the maze from YouTube: https://youtu.be/03VlOt-UVdA

The following information about enemy settings may be helpful for you:

Monster                 HP             Attack            Defence              Gain$

White Slime             10                2                 1                  10
Black Slime             20                3                 1                  20
Small Bat               30                5                 1                  20 
Large Bat              150               15                10                  30
Skeleton               100                5                14                  10
White Mage             120               15                15                  20
Black Mage             120               17                25                  30
White Guard            200               20                25                  50
Black Guard            300               30                35                 100
Devil                  780               50                50                 660

The harm to you from a fight is calculated as:

harm to you = monster's HP / (your attack - monster's defence) * (monster's attack - your defence)

If your attack is lower than the moster's defence, the harm is 9999,
as you cannot hurt the enemy.

If the monster's attack is lower than your defence, then your harm is 0.

Hope you enjoy the game!


Wang Yihao
2/5/2019