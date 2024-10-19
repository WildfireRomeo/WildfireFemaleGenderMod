# Armor Resource Configs

Armor physics values can be modified by a resource pack by placing a JSON file at `assets/NAMESPACE/wildfire_gender_data/MODEL.json`,
where `NAMESPACE` and `MODEL` are replaced with the respective values (such as `assets/minecraft/wildfire_gender_data/iron.json`).

The full schema with default values is as follows:

```json5
{
  // both values are a range of 0.0 and 1.0 (inclusive)
  "resistance": 0.5,
  "tightness": 0.0,
  "covers_breasts": true,
  "hide_breasts": false,
  "render_on_armor_stands": null, // true if resistance == 1
  "texture": {
    "texture_size": {"x": 64, "y": 32},
    "dimensions": {"x": 4, "y": 5},
    "left_uv": {"x": 16, "y": 17},
    "right_uv": {"x": -1, "y": -1} // defaults to left_uv added with the x value of dimensions
  }
}
```

## Values

### `resistance`

A number value between `0.0` and `1.0` (inclusive) determining how much this armor resists the wearer's breast physics as
a percentage value, where values like `0.5` equate to 50%.

Defaults to `0.5` (50%) if not set.

### `tightness`

A number value between `0.0` and `1.0` (inclusive) determining how much this armor "compresses" the wearer's breasts against
their chest as a percentage value, making them appear up to 15% smaller at `1.0` (100%).

Defaults to `0.0` (0%) if not set.

### `covers_breasts`

Boolean value determining if this armor piece covers the wearer's breasts; this is intended to be set to `false` for equippable
chest slot items with an open front, such as the Elytra. Note that this does *not* prevent the item from rendering over the wearer's
breasts.

Defaults to `true` if not set.

### `hide_breasts`

Boolean value determining if the wearer's breasts should be hidden entirely while this armor piece is worn; this is
intended for armor that use custom rendering which is largely incompatible with how this mod's breasts render.

Defaults to `false` if not set.

### `render_on_armor_stands`

Boolean value determining if armor stands should render the breast settings of the player equipping this armor piece
onto them.

This is designed for armor types which are metallic in nature (such as Iron and Gold), and not ones which would be
flexible enough to accommodate for the wearer's breasts on their own (such as Leather and Chain).

Defaults to `true` *only* if `resistance` is `1.0` if unset.

### `texture`

Object containing various texture-related overrides; note that all values that specify `{"x": ..., "y": ...}`
*must* contain both `x` and `y` if specified.

#### `texture_size`

Controls the armor sprite's texture size.

Note that if your sprite is simply an upscaled resolution of the vanilla sprite size (such as 128x64, 256x128, etc.)
then you do *not* need to modify this or any other texture values.

Defaults to `{"x": 64, "y": 32}` if unset.

#### `dimensions`

Controls how large an area the breasts should grab from the sprite for *each breast*; this means that this value's
X is *half* of the total sprite area to render.

Defaults to `{"x": 4, "y": 5}` if unset.

#### `left_uv`

Controls where the left breast should start rendering this armor from.

Defaults to `{"x": 16, "y": 17}` if unset.

#### `right_uv`

Controls where the right breast should start rendering this armor from.

Defaults to `left_uv` added with the X value of `dimensions` if unset.
