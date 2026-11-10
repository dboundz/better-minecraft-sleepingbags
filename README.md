# Sleeping Bag

A NeoForge 1.21.1 mod. Place a wool bag on the ground and sleep in it at night. It does not set your spawn.

## Download

- [sleepingbag-1.0.1.jar](https://github.com/dboundz/better-minecraft-sleepingbags/raw/main/dist/sleepingbag-1.0.1.jar)

Needs Minecraft **1.21.1** and NeoForge **21.1.265**. Put the jar in the `mods` folder on the server and on every client, then restart.

## Crafting

All three wool pieces must be the same color. That color is the bag. There is one bag for each of the 16 dye colors.

```
F F F
W M W
  W
```

- F is a feather
- W is wool, all three the same color
- M is a phantom membrane

A bag and one dye, anywhere in the crafting grid, recolors it. Durability and enchantments stay.

## What it can do

- Place it on the ground like a bed. It is two blocks long, makes a wool sound, and has no wooden frame.
- Right-click the head at night to lie down, including when it is raining. Your head rests on whatever block the bag is sitting on, and the quilt covers your body.
- Falling asleep takes 10 seconds. After that you count toward the server's sleep percentage, the same way a bed does.
- Getting out before those 10 seconds are up spends nothing.
- A finished sleep spends 1 durability. A bag lasts 10 sleeps.
- Breaking either half picks the bag back up with its color, damage, and enchantments. If someone is in it, they wake up.
- It accepts three enchantments: Unbreaking, Mending, and Quick Nap. Unbreaking and Mending work from the enchanting table or an anvil. Quick Nap is one level, and it is in the enchanting table and on the anvil too. With Quick Nap, falling asleep takes 5 seconds.
- Mending repairs the bag while you hold it and pick up experience.

## What it cannot do

- It does not set your spawn.
- It cannot be used during the day.
- It cannot be used during a thunderstorm, even at night.
- It cannot be used in the Nether or the End, and it does not explode there.
- It cannot be used if monsters are nearby, you are too far from it, or the space above it is blocked. Those failures match a bed.
- One bag holds one player.
- It does not stack.
- When the last durability is spent, the bag is destroyed and does not drop.
- Mending does not repair a bag that is sitting on the ground.
- No other enchantments apply.
