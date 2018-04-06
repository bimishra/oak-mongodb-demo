package org.samples.oak.test;

import org.junit.Assert;
import org.junit.Test;
import org.samples.oak.demo.type.NodeType;

public class TestNodeType {

    @Test
    public void shouldGetValue() {
        Assert.assertEquals(NodeType.FILE.getValue(), "file_node");
    }

    @Test
    public void shouldGetEnum() {
        Assert.assertTrue(NodeType.FILE == NodeType.toEnum("file_node"));
    }
}
