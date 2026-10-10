import com.jagex.math.ColourUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Where a model draws its billboards, for a shape's {@code extras}: for each, the three corners
 * {@code a}, {@code b} and {@code c} of its face in the client's units and frame before the file's
 * turn, as an emitter's are, and the {@code labels} the corners' vertices carry, -1 for none, as
 * the GL toolkit draws a billboard at the middle of its face's corners after a sequence's frame has
 * moved them; how far it is pulled towards the camera, its half width and half height in the
 * client's units, its texture, how it blends (1 by its alpha, 2 added, 128 multiplied in), the
 * face's colour as the palette gives it, its alpha out of 255, and its group, by which the frames of a
 * sequence scale, turn and move it (transforms 10, 9 and 8, {@code Mesh.getBillboardGroups}), -1
 * for none. These are what the GL toolkit draws with ({@code GlModel.renderBillboards}); the face
 * itself is left out of the mesh where the type hides it.
 */
public final class BillboardSources {

    private static final int WHOLE_ALPHA = 255;

    private static final int NO_GROUP = -1;

    private static final int NO_LABEL = -1;

    public static List<Map<String, Object>> billboards(JavaModel model) {
        var written = new ArrayList<Map<String, Object>>();
        for (var index = 0; index < model.billboardFaces.length; index++) {
            var billboard = model.billboardFaces[index];
            var face = billboard.face;
            var a = model.faceA[face];
            var b = model.faceB[face];
            var c = model.faceC[face];
            var entry = new LinkedHashMap<String, Object>();
            entry.put("a", corner(model, a));
            entry.put("b", corner(model, b));
            entry.put("c", corner(model, c));
            entry.put("labels", List.of(vertexLabel(model, a), vertexLabel(model, b), vertexLabel(model, c)));
            entry.put("distance", billboard.distance);
            entry.put("halfWidth", (int) billboard.width);
            entry.put("halfHeight", (int) billboard.height);
            entry.put("texture", (int) billboard.texture);
            entry.put("blendMode", (int) billboard.blendMode);
            entry.put("colour", ColourUtils.HSL_TO_RGB[model.faceColour[face] & 0xFFFF] & 0xFFFFFF);
            entry.put("alpha", WHOLE_ALPHA - (model.faceAlpha == null ? 0 : model.faceAlpha[face] & 0xFF));
            entry.put("group", groupOf(model, index));
            written.add(entry);
        }
        return written;
    }

    /**
     * The group of a billboard, from the billboards of each group the model keeps where it was
     * built with their labels, or none.
     */
    private static int groupOf(JavaModel model, int billboard) {
        if (model.billboardLabels == null) {
            return NO_GROUP;
        }
        for (var group = 0; group < model.billboardLabels.length; group++) {
            for (var member : model.billboardLabels[group]) {
                if (member == billboard) {
                    return group;
                }
            }
        }
        return NO_GROUP;
    }

    private static List<Integer> corner(JavaModel model, int vertex) {
        return List.of(model.vertexX[vertex], model.vertexY[vertex], model.vertexZ[vertex]);
    }

    /**
     * The label a vertex carries, from the vertices of each label the model keeps where it was
     * built with its labels, or none.
     */
    private static int vertexLabel(JavaModel model, int vertex) {
        if (model.vertexLabels == null) {
            return NO_LABEL;
        }
        for (var label = 0; label < model.vertexLabels.length; label++) {
            for (var member : model.vertexLabels[label]) {
                if (member == vertex) {
                    return label;
                }
            }
        }
        return NO_LABEL;
    }

    private BillboardSources() {
        /* empty */
    }
}
