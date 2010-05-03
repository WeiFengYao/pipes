package com.tinkerpop.pipes.serial;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.pipes.serial.pgm.EdgeVertexPipe;
import com.tinkerpop.pipes.serial.pgm.LabelFilterPipe;
import com.tinkerpop.pipes.serial.pgm.VertexEdgePipe;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * @author: Marko A. Rodriguez (http://markorodriguez.com)
 */
public class PipelineTest extends TestCase {

    public void testOneStagePipeline() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        Vertex marko = graph.getVertex("1");
        Pipe vep = new VertexEdgePipe(VertexEdgePipe.Step.OUT_EDGES);
        Pipe<Vertex, Edge> pipeline = new Pipeline<Vertex, Edge>(Arrays.asList(vep));
        pipeline.setStarts(Arrays.asList(marko).iterator());
        assertTrue(pipeline.hasNext());
        int counter = 0;
        while (pipeline.hasNext()) {
            Edge e = pipeline.next();
            assertTrue(e.getInVertex().getId().equals("4") || e.getInVertex().getId().equals("2") || e.getInVertex().getId().equals("3"));
            counter++;
        }
        assertEquals(3, counter);


    }

    public void testThreeStagePipeline() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        Vertex marko = graph.getVertex("1");
        Pipe vep = new VertexEdgePipe(VertexEdgePipe.Step.OUT_EDGES);
        Pipe lfp = new LabelFilterPipe(Arrays.asList("created"), false);
        Pipe evp = new EdgeVertexPipe(EdgeVertexPipe.Step.IN_VERTEX);
        Pipe<Vertex, Vertex> pipeline = new Pipeline<Vertex, Vertex>(Arrays.asList(vep, lfp, evp));
        pipeline.setStarts(Arrays.asList(marko).iterator());
        assertTrue(pipeline.hasNext());
        int counter = 0;
        while (pipeline.hasNext()) {
            assertEquals(pipeline.next().getId(), "3");
            counter++;
        }
        assertEquals(1, counter);

        vep = new VertexEdgePipe(VertexEdgePipe.Step.OUT_EDGES);
        lfp = new LabelFilterPipe(Arrays.asList("created"), true);
        evp = new EdgeVertexPipe(EdgeVertexPipe.Step.IN_VERTEX);
        pipeline = new Pipeline<Vertex, Vertex>(Arrays.asList(vep, lfp, evp));
        pipeline.setStarts(Arrays.asList(marko).iterator());
        assertTrue(pipeline.hasNext());
        counter = 0;
        while (pipeline.hasNext()) {
            Vertex v = pipeline.next();
            assertTrue(v.getId().equals("4") || v.getId().equals("2"));
            counter++;
        }
        assertEquals(2, counter);
        try {
            pipeline.next();
            assertTrue(false);
        } catch (NoSuchElementException e) {
            assertFalse(false);
        }

    }

    public void testPipelineResuse() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        Vertex marko = graph.getVertex("1");
        Pipe vep = new VertexEdgePipe(VertexEdgePipe.Step.OUT_EDGES);
        Pipe evp = new EdgeVertexPipe(EdgeVertexPipe.Step.IN_VERTEX);
        Pipe<Vertex, Vertex> pipeline = new Pipeline<Vertex, Vertex>(Arrays.asList(vep, evp));
        pipeline.setStarts(Arrays.asList(marko).iterator());
        assertTrue(pipeline.hasNext());
        int counter = 0;
        if (pipeline.hasNext()) {
            counter++;
            pipeline.next();
        }
        assertEquals(1, counter);

        pipeline.setStarts(Arrays.asList(marko).iterator());
        assertTrue(pipeline.hasNext());
        counter = 0;
        while (pipeline.hasNext()) {
            counter++;
            pipeline.next();
        }
        assertEquals(5, counter);
        try {
            pipeline.next();
            assertTrue(false);
        } catch (NoSuchElementException e) {
            assertFalse(false);
        }
    }

}