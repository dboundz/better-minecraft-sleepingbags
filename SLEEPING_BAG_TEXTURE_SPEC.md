# Sleeping Bag Texture Spec

Temporary flat textures are already in the paths below. Replace the PNG files in place. Do not change pixel dimensions, and do not rename the files. The block models and the cover cuboid read fixed UV rectangles from these images.

All art is hard-edged pixel art. No anti-aliasing, no gradients, and no partial alpha on a pixel that is meant to be fabric. Unused pixels may be fully transparent.

Dye works by multiplication. White `(255, 255, 255)` becomes the wool color. Gray becomes a darker shade of that same wool color. The pillow is not multiplied, so its cream pixels stay cream on every bag.

The 16 bags do not have separate textures. `SleepingBagClient` tints every quilt face that has `tintindex` 0, and the occupied cover multiplies the same way. The colors are the vanilla dye colors: white, orange, magenta, light blue, yellow, lime, pink, gray, light gray, cyan, purple, blue, brown, green, red, black.

The inventory icon `assets/sleepingbag/textures/item/sleeping_bag.png` is not part of this layout. Leave it alone.

## Files

| File | Size | Alpha | Tinted |
| --- | --- | --- | --- |
| `src/main/resources/assets/sleepingbag/textures/block/quilt.png` | 16 × 32 | Rows 18–31 are unused and transparent | Yes |
| `src/main/resources/assets/sleepingbag/textures/block/pillow.png` | 16 × 16 | Unused pixels outside the rectangles below may be transparent | No |
| `src/main/resources/assets/sleepingbag/textures/block/cover.png` | 16 × 16 | Fully opaque | Yes |

## Quilt

`quilt.png` is the empty bag's blanket, sides, and closed foot. North in the model is the pillow end. South is the closed foot.

```
y  0 ┌──────────────── white top, 16×16 ────────────────┐
     │  stitches at x = 4, 8, 12 and y = 4, 8, 12       │
y 16 ├──────────────── darker edge, 16×2 ───────────────┤
y 18 ├──────────────── unused, transparent ─────────────┤
y 32 └──────────────────────────────────────────────────┘
```

Suggested temporary colors, which the game is using now:

- Top base: `(255, 255, 255)`
- Stitch: `(176, 176, 176)`
- Edge: `(140, 140, 140)`

Keep the top base white. A gray stitch or edge is the only darker shade, and it becomes the darker wool shade after tinting. Do not paint the pillow into this file.

### Top face, texture y 0–16

The foot block's main slab is model `[0, 0, 0]` to `[16, 2, 15]`. Its top and bottom use UV `[0, 0, 16, 15]`. Texture v = 0 is the north edge, against the head block. Texture v = 15 is one pixel before the closed end.

The closed end cap is model `[1, 0, 15]` to `[15, 2, 16]`. The x range starts at 1 and ends at 15, which cuts both south corners by one pixel. Its top and bottom use UV `[1, 15, 15, 16]`.

The head block's main slab is model `[0, 0, 2]` to `[16, 2, 16]`. Its top and bottom use UV `[0, 14, 16, 0]`, flipped so the south edge (the join with the foot) also reads texture v = 0. Paint that shared row as one continuous stitch, not a seam.

The head's north lip is model `[1, 0, 1]` to `[15, 2, 2]`. The x inset cuts both north corners by one pixel. Its top and bottom use UV `[1, 15, 15, 16]`.

### Sides and closed end, texture y 16–18

Every vertical face reads this 2-pixel-tall strip.

- Foot east and west, 15 pixels long: UV `[0, 16, 15, 18]`
- Closed south end, 14 pixels wide: UV `[1, 16, 15, 18]`
- South cap's small east and west ends: UV `[15, 16, 16, 18]` and `[0, 16, 1, 18]`
- Head east and west, 14 pixels long: UV `[2, 16, 16, 18]`
- Head north lip: UV `[1, 16, 15, 18]`, with 1-pixel east and west ends at UV `[0, 16, 1, 18]`

The foot has no north face and the head slab has no south face. Those two ends meet inside the bag and are not textured.

## Pillow

`pillow.png` is only the cream pillow on the head block. The cuboid is model `[2, 2, 2]` to `[14, 4, 8]`: 12 wide, 6 long, and 2 pixels tall, sitting on the 2-pixel quilt so its top is 4 pixels above the ground. It is inset from the bag's sides. None of its faces have a tint index.

```
y  0 ┌──────── cream top ────────┐
     │  UV [2, 2, 14, 8] is the  │
     │  12×6 top face            │
y  8 ├──────── side shade ───────┤
y 10 │  UV y 8–10, the four sides │
y 16 └──────── unused ───────────┘
```

Suggested colors:

- Top field: `(243, 230, 200)`
- Top inner highlight, pixels x 3–12 and y 2–6: `(255, 246, 228)`
- Sides, rows 8–10: `(217, 199, 160)`

The bottom face is not rendered. Do not use white here if the pillow should stay cream, because this image is not tinted.

## Occupied cover

`cover.png` is the quilt pulled over the player. It is a separate cuboid, not a block model, drawn only while that player is actually asleep in a bag. Waking or breaking the bag removes it. The head, hair, and hat stay visible. The body, arms, and legs are hidden under this cuboid.

The cuboid is parented to the player body: x −7 to 7, y 0 to 24, z −3 to 2. That runs from the shoulders to the feet and replaces the hidden torso. The whole 16×16 image is sampled by that cuboid and then multiplied by the wool color, so every pixel should be the blanket base, normally white. Stitch pixels may be the same gray as the quilt stitches. Do not draw the pillow in this file.

## Replacing the temporary art

1. Export each PNG at the exact size in the table, with hard pixels.
2. Overwrite the three files. Leave the model JSON alone unless a rectangle above is no longer the one you painted.
3. Restart the client. The server does not draw these textures, but players need the same jar to see them.
