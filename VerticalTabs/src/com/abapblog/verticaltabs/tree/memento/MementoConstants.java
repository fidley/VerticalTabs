package com.abapblog.verticaltabs.tree.memento;

public abstract class MementoConstants {
	public abstract static class Types {
		public static final String GroupNodeChild = "GroupNodeChild";
		public static final String TreeContentProvider = "TreeContentProvider";
		public static final String TreeNodes = "TreeNodes";
		public static final String GroupNodes = "GroupNodes";
		public static final String TabNodes = "TabNodes";
		public static final String TabNode = "TabNode";
		public static final String GroupNode = "GroupNode";
		public static final String ProjectNodes = "ProjectNodes";
		public static final String ProjectNode = "ProjectNode";
	}

	public abstract static class Ids {
		public static final String TreeContentProvider = "com.abapblog.verticalTabs.TreeContentProviderMemento";
		public static final String TreeNodes = "com.abapblog.verticalTabs.TreeNodesMemento";
		public static final String TabNodes = "com.abapblog.verticalTabs.TabNodesMemento";
		public static final String GroupNodes = "com.abapblog.verticalTabs.GroupNodesMemento";
		public static final String TabNode = "com.abapblog.verticalTabs.TabNodeMemento";
		public static final String GroupNode = "com.abapblog.verticalTabs.GroupNodeMemento";
		public static final String ProjectNodes = "com.abapblog.verticalTabs.ProjectNodesMemento";
		public static final String ProjectNode = "com.abapblog.verticalTabs.ProjectNodeMemento";
		public static final String GroupNodeChild = "com.abapblog.verticalTabs.GroupNodeChildMemento";;
	}

	public abstract static class Keys {
		public static final String NodeName = "Name";
		public static final String NodeProject = "Project";
		public static final String NodePath = "Path";
		public static final String NodeSortIndex = "SortIndex";
		public static final String NodeEditorId = "EditorID";
		public static final String NodeExpanded = "Expanded";
		public static final String NodeID = "Expanded";
		public static final String NodePinned = "Pinned";
	}

}
