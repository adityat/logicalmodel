package org.colomoto.logicalmodel.tool.simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.LogicalModelImpl;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.tool.simulation.updater.AsynchronousUpdater;
import org.colomoto.logicalmodel.tool.simulation.updater.SequentialUpdater;
import org.colomoto.logicalmodel.tool.simulation.updater.SynchronousUpdater;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.internal.MDDStoreImpl;
import org.colomoto.mddlib.operators.MDDBaseOperators;
import org.junit.Assert;
import org.junit.Test;

public class TestUpdaters {

	private LogicalModel getModel() {
		// build a list of variables and functions for a model
		List<NodeInfo> vars = new ArrayList<NodeInfo>();
		vars.add(new NodeInfo("A"));
		vars.add(new NodeInfo("B"));
		vars.add(new NodeInfo("C"));
		
		MDDManager manager = new MDDStoreImpl(vars, 2);
		int[] functions = new int[vars.size()];
		functions[0] = 1;
		functions[1] = 1;
		MDDVariable va = manager.getVariableForKey(vars.get(0));
		MDDVariable vb = manager.getVariableForKey(vars.get(1));
		int fa = va.getNode(0, 1);
		int fb = vb.getNode(0, 1);
		functions[2] = MDDBaseOperators.AND.combine(manager, fa, fb);
		
		return new LogicalModelImpl(vars, manager, functions);
	}
	
	@Test
	public void testAsynchronousUpdater() throws IOException {
		LogicalModel model = getModel();
		LogicalModelUpdater updater = new AsynchronousUpdater(model);
		byte[] state = {0,0,0};
		updater.setState(state);
		
		Iterator<byte[]> it = updater.iterator();
		Assert.assertEquals(true, it.hasNext());
		byte[] next = it.next();
		Assert.assertEquals(1, next[0]);
		Assert.assertEquals(0, next[1]);
		Assert.assertEquals(0, next[2]);

		Assert.assertEquals(true, it.hasNext());
		next = it.next();
		Assert.assertEquals(0, next[0]);
		Assert.assertEquals(1, next[1]);
		Assert.assertEquals(0, next[2]);

		Assert.assertEquals(false, it.hasNext());
	}

	@Test
	public void testSynchronousUpdater() throws IOException {
		LogicalModel model = getModel();
		LogicalModelUpdater updater = new SynchronousUpdater(model);
		byte[] state = {0,0,0};
		updater.setState(state);
		
		Iterator<byte[]> it = updater.iterator();
		Assert.assertEquals(true, it.hasNext());
		byte[] next = it.next();
		Assert.assertEquals(1, next[0]);
		Assert.assertEquals(1, next[1]);
		Assert.assertEquals(0, next[2]);

		Assert.assertEquals(false, it.hasNext());
	}

	@Test
	public void testSequentialUpdater() throws IOException {
		LogicalModel model = getModel();
		LogicalModelUpdater updater = new SequentialUpdater(model);
		byte[] state = {0,0,0};
		updater.setState(state);
		
		Iterator<byte[]> it = updater.iterator();
		Assert.assertEquals(true, it.hasNext());
		byte[] next = it.next();
		Assert.assertEquals(1, next[0]);
		Assert.assertEquals(1, next[1]);
		Assert.assertEquals(1, next[2]);

		Assert.assertEquals(false, it.hasNext());
	}

	@Test
	public void testCustomSequentialUpdater() throws IOException {
		LogicalModel model = getModel();
		int[] order = {1,2,0};
		LogicalModelUpdater updater = new SequentialUpdater(model, order);
		byte[] state = {0,0,0};
		updater.setState(state);
		
		Iterator<byte[]> it = updater.iterator();
		Assert.assertEquals(true, it.hasNext());
		byte[] next = it.next();
		Assert.assertEquals(1, next[0]);
		Assert.assertEquals(1, next[1]);
		Assert.assertEquals(0, next[2]);

		Assert.assertEquals(false, it.hasNext());
	}
}
