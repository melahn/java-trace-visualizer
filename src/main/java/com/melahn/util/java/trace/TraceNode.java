package com.melahn.util.java.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TraceNode implements Comparable<TraceNode> {
    int id;
    String methodName;
    String lineNumber;
    int depth;
    TraceNode parent = null;
    boolean isPrinted = false;
    Set<TraceNode> children = new HashSet<>();

    TraceNode(int i, String m, String l, int d, TraceNode p) {
        id = i;
        methodName = m;
        lineNumber = l;
        depth = d;
        isPrinted = false;
        parent = p;
        if (parent != null) {
            p.children.add(this);
        }
    }

    @Override
    public int compareTo(TraceNode n) {
        return this.id - n.id;
    }

    @Override
    public final int hashCode() {
        return 31 * 17 + methodName.concat(String.valueOf(id)).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && this.getClass() != o.getClass()) {
            return (((TraceNode) o).id == id);
        }
        return false;
    }

    /**
     * Returns a set of the ancestors of this node that have children that have not
     * yet been printed. This is useful information when printing vertices of an ascii text
     * tree view of the trace, for example.
     * 
     * @return a reversed list of ancestor TraceNodes 
     */
    List<TraceNode> getUnprintedAncestors() {
        List<TraceNode> a = new ArrayList<>(); 
        TraceNode p = parent;
        while (p != null) {
            Iterator<TraceNode> i = p.children.iterator();
            boolean include = false;
            while(i.hasNext() && !include) {
                TraceNode n = i.next();
                if (!n.isPrinted) {
                   include = true;
               }
               if (include) {
                a.add(n);
               }
            }
            p = p.parent;
        }
        Collections.reverse(a);
        return a;
    }
}
