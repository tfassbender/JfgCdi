package net.jfabricationgames.cdi;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import net.jfabricationgames.cdi.testClasses.beans.DependentClass;

/**
 * Test the use of the {@link CdiContainer} in multiple threads.
 */
public class ConcurrentTest {
	
	@BeforeEach
	public void initializeCdiContainer() throws IOException {
		CdiContainer.create("net.jfabricationgames.cdi");
	}
	
	@AfterEach
	public void destroyCdiContainer() {
		CdiContainer.destroy();
	}
	
	/**
	 * @throws InterruptedException
	 */
	@RepeatedTest(20)
	public void testApplicationScopedInstancesAreSharedOverMultipleThreads() throws InterruptedException {
		final int numThreads = 5;
		List<DependentClass> dependentInstances = new ArrayList<>();
		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			threads.add(new Thread(() -> {
				dependentInstances.add(new DependentClass()); // creating a dependent class will inject the dependencies into the object
			}));
		}
		
		threads.forEach(Thread::start);
		
		Thread.sleep(10); // wait for the threads to finish
		
		for (int i = 0; i < dependentInstances.size() - 1; i++) {
			assertTrue(dependentInstances.get(i).getApplicationScopedBean() == dependentInstances.get(i + 1).getApplicationScopedBean(), //
					"Instances " + i + " and " + (i + 1) + " must share the same application scoped bean");
		}
	}
}
