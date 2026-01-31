Texture in LSS
Since 2.1.4

In LDLib2, many visual styles (such as background) accept a texture value.
Texture values in LSS are string-based expressions that describe how a texture is created and optionally transformed.

This system is flexible and composable, allowing you to build complex visuals using a concise syntax.

Supported Texture Types
Empty Texture

background: empty;
or an empty string:


background: ;
This results in no texture being rendered.

Solid Color
If the value can be parsed as a color, it will be treated as a color rectangle:


background: #1F00FFFF; /* #AARRGGBB */
background: #FF00FF; //* #RRGGBB */
background: #FFF; /* #RGB */
background: rgba(255, 0, 0, 128);
background: rgb(255, 123, 0);
sprite(...)
Uses a sprite from a texture atlas or resource location.


background: sprite(ldlib2:textures/gui/icon.png);
Advanced usage:


background: sprite(
    ldlib2:textures/gui/icon.png,
    0, 0, 16, 16,          /* sprite region (optional) */
    2, 2, 2, 2,            /* border (opional) */
    #FFFFFF                /* color tint (optional) */
);
icon(...)
Uses a registered icon texture.


background: icon(check);
background: icon(modid, check);
rect(...) / sdf(...)
Creates an SDF-based rectangle texture.


background: rect(#FF0000);
background: rect(#FF0000, 4);
background: rect(#FF0000, 4 4 4 4, 2, #FFFFFF);
Arguments:

Fill color
Corner radius (single value or 4 values) (optional)
Stroke width (optional)
Border color (optional)
border(...)
Creates a simple colored border.


background: border(2, #00FF00); 
group(...)
Combines multiple textures into a single group.


background: group(
    sprite(ldlib2:textures/gui/bg.png),
    rect(#FFFFFF, 2)
);
shader(...)
Uses a custom shader texture.


background: shader(ldlib2:fbm);
Resource-based Textures
If the function name matches a registered resource provider type, it will be resolved automatically:


background: builtin(ui-gdp:BORDER);
background: file("<namespace>:<path>");
This allows integration with LDLib2’s resource system.

Texture Transforms
After the main texture, you can apply transform functions.

Scale

background: sprite(...) scale(2);
background: sprite(...) scale(2, 1);
Rotate

background: sprite(...) rotate(45);
Translate

background: sprite(...) translate(4, 8);
Color Override

background: sprite(...) color(#FFAA00);
background: sprite(...) color(255, 255, 0, 0);
This tints the texture using the given color.

In summary, LSS texture values let you:

Define textures declaratively
Chain transforms in a readable way
Reuse and override visuals efficiently
This makes UI styling both powerful and resource-pack friendly.