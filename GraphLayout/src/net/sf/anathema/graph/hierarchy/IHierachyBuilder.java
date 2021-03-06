package net.sf.anathema.graph.hierarchy;

import net.sf.anathema.graph.nodes.IRegularNode;
import net.sf.anathema.graph.nodes.ISimpleNode;

public interface IHierachyBuilder {

  ISimpleNode[] removeLongEdges(IRegularNode[] acyclicGraph);

}