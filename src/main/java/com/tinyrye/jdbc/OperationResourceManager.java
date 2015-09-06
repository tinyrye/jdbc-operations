package com.tinyrye.jdbc;

import java.util.ArrayList;
import java.util.List;

public class OperationResourceManager
{
    private final List<AutoCloseable> openResources = new ArrayList<AutoCloseable>();

    public OperationResourceManager() {

    }

    public OperationResourceManager(AutoCloseable ... openResources) {
        add(openResources);
    }

    public OperationResourceManager(OperationResourceManager previous) {
        this.openResources.addAll(previous.openResources);
        previous.openResources.clear();
    }

    public boolean contains(AutoCloseable openResource) {
        return openResources.contains(openResource);
    }
    
    public OperationResourceManager add(AutoCloseable ... openResources)
    {
        for (AutoCloseable openResource: openResources) {
            this.openResources.add(openResource);
        }
        return this;
    }

    public OperationResourceManager takeoverFor(OperationResourceManager resourceManager) {
        openResources.addAll(resourceManager.openResources);
        resourceManager.absolve();
        return this;
    }

    /**
     * This manager is no longer responsible for its resources.
     */
    public void absolve() {
        openResources.clear();
    }
    
    public void close() {
        for (int i = openResources.size() - 1; i >= 0; i--) {
            closeQuietly(openResources.get(i));
        }
    }

    /**
     * Support method for you if you have Statements, ResultSets, etc. to close.
     * Usage is typically:
     * <code>
     *   statement = close(statement);
     * </code>
     * @return <code>null</code> unconditially so you can set your resource to null
     * after closure
     */
    protected static void closeQuietly(AutoCloseable resource) {
        try { if (resource != null) resource.close(); }
        catch (Exception ex) { /* shhhh! */ }
    }
}