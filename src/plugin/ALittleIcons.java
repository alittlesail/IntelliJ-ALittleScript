package plugin;

import com.intellij.icons.AllIcons;
import com.intellij.ui.IconManager;

import javax.swing.*;

public class ALittleIcons {
    public static final Icon FILE = IconManager.getInstance().getIcon("/icons/file_icon.png", ALittleIcons.class);
    public static final Icon MODULE = IconManager.getInstance().getIcon("/icons/module_icon.png", ALittleIcons.class);

    public static final Icon CLASS = AllIcons.Nodes.Class;
    public static final Icon NAMESPACE = AllIcons.Nodes.Package;
    public static final Icon STRUCT = AllIcons.Nodes.DataTables;
    public static final Icon ENUM = AllIcons.Nodes.Enum;
    public static final Icon INSTANCE = AllIcons.Nodes.AnonymousClass;

    public static final Icon METHOD = AllIcons.Nodes.Method;
    public static final Icon STATIC_METHOD = AllIcons.Nodes.Static;
    public static final Icon GLOBAL_METHOD = AllIcons.Nodes.Static;
    public static final Icon MEMBER_METHOD = AllIcons.Nodes.Method;
    public static final Icon GETTER_METHOD = AllIcons.Nodes.Field;
    public static final Icon SETTER_METHOD = AllIcons.Nodes.Field;

    public static final Icon PARAM = AllIcons.Nodes.Parameter;
    public static final Icon VARIABLE = AllIcons.Nodes.Variable;
    public static final Icon PROPERTY = AllIcons.Nodes.Property;

    public static final Icon OVERRIDE = AllIcons.Nodes.Interface;
}