/*
 Copyright (c) 2006-2012 the original author or authors.

 Redistribution and use of this software in source and binary forms,
 with or without modification, are permitted provided that the following
 conditions are met:

 * Redistributions of source code must retain the above
   copyright notice, this list of conditions and the
   following disclaimer.

 * Redistributions in binary form must reproduce the above
   copyright notice, this list of conditions and the
   following disclaimer in the documentation and/or other
   materials provided with the distribution.

 * The name of Web-Harvest may not be used to endorse or promote
   products derived from this software without specific prior
   written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
*/

package org.webharvest.definition;

import java.util.LinkedList;
import java.util.List;

/**
 * Abstract {@link ConfigurableResolver} implementation serving as a base class
 * for definition resolvers supposed to support multiple refresh operations.
 * Each time, resolver refresh operation is invoked, new instance of the
 * internal {@link ElementsRegistry} is created and all previously registered
 * {@link ResolverPostProcessor}s are triggered.
 *
 * @author Piotr Dyraga
 * @since 2.1.0-SNAPSHOT
 * @version %I%, %G%
 */
public abstract class AbstractRefreshableResolver implements
        ConfigurableResolver {

    private ElementsRegistry elementsRegistry;

    private final List<ResolverPostProcessor> postProcessors =
        new LinkedList<ResolverPostProcessor>();

    /** Synchronization monitor for the "refresh" operation */
    private final Object refreshMonitor = new Object();

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addPostProcessor(
            final ResolverPostProcessor postProcessor) {
        this.postProcessors.add(postProcessor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void refresh() {
        synchronized (this.refreshMonitor) {
            this.elementsRegistry = createElementsRegistry();
            invokePostProcessors();
        }
    }

    private void invokePostProcessors() {
        for (ResolverPostProcessor postProcessor : this.postProcessors) {
            postProcessor.postProcess(this);
        }
    }

    /**
     * Returns current instance of the {@link ElementsRegistry}.
     */
    protected final ElementsRegistry getElementsRegistry() {
        return this.elementsRegistry;
    }

    /**
     * Creates internal {@link ElementsRegistry} for the current resolver each
     * time {@link #refresh()} is invoked. Subclasses may override this method
     * providing custom {@link ElementsRegistry} implementations if necessary.
     */
    protected ElementsRegistry createElementsRegistry() {
        return new ElementsRegistryImpl();
    }
}
