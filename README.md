# ReplaceBlocksMod

A utility mod that lets you replace blocks with other blocks, used entirely
through the console.

The provided commands are intended to be more convenient than using the
vanilla `/fill` command.

## Commands

### 1. `/replace <sourceBlock> with <targetBlock>`

Replaces all nearby instances of `sourceBlock` with `targetBlock`.

e.g. `/replace oak_planks with cherry_planks`

In Vanilla, this would be equivalent to
`/fill ~-50 ~-10 ~-50 ~50 ~10 ~50 oak_planks replace cherry_planks`
which is much more difficult to type and fails anyway because
the `/fill` command can't handle that many blocks.

***

### 2. `/removegrass`

Removes all nearby short grass and long grass.
Regular grass blocks will not be removed.

In Vanilla, this would be similar to (but more optimized than)
`/fill ~-50 ~-10 ~-50 ~50 ~10 ~50 air replace short_grass`
and `/fill ~-50 ~-10 ~-50 ~50 ~10 ~50 air replace tall_grass`
but again, the `/fill` command can't handle that many blocks.
