/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opentravel.schemas.trees.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IDecorationContext;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.progress.WorkbenchJob;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemas.actions.AddToProjectAction;
import org.opentravel.schemas.actions.SyncRepositoryAction;
import org.opentravel.schemas.controllers.ProjectController;
import org.opentravel.schemas.controllers.RepositoryController;
import org.opentravel.schemas.node.Node;
import org.opentravel.schemas.node.ProjectNode;
import org.opentravel.schemas.node.interfaces.INode;
import org.opentravel.schemas.preferences.DefaultPreferences;
import org.opentravel.schemas.properties.DefaultStringProperties;
import org.opentravel.schemas.properties.PropertyType;
import org.opentravel.schemas.properties.StringProperties;
import org.opentravel.schemas.stl2developer.OtmRegistry;
import org.opentravel.schemas.trees.repository.RepositoryNode.RepositoryItemNode;
import org.opentravel.schemas.trees.repository.RepositoryTreeContentProvider.RepositoryTreeComparator;
import org.opentravel.schemas.trees.repository.RepositoryTreeContentProvider.RepositoryTreeLabelProvider;

/**
 * Extend the treeViewer with menus and refresh behavior. Define menu managers and sets of menus. Define and instantiate
 * actions. Define members of the menus. Implement the menu listener
 * 
 * @author Dave Hollander
 * 
 */
public class RepositoryMenus {

	private MenuManager menuManager;
	private TreeViewer viewer;
	private volatile boolean includeRepositorySearch = DefaultPreferences.getRepositoryRemoteSearch();
	private SearchRepositoryTree filteredTree;

	class SearchRepositoryTree extends FilteredTree {

		public SearchRepositoryTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook) {
			super(parent, treeStyle, filter, useNewLook);
		}

		@Override
		protected long getRefreshJobDelay() {
			return 500;
		}

		@Override
		protected WorkbenchJob doCreateRefreshJob() {
			WorkbenchJob refreshJob = super.doCreateRefreshJob();
			refreshJob.addJobChangeListener(new JobChangeAdapter() {

				@Override
				public void running(IJobChangeEvent event) {
					final RepositorySearchPatter pattern = (RepositorySearchPatter) SearchRepositoryTree.this
							.getPatternFilter();
					if (includeRepositorySearch) {
						searchRepository(pattern);
						pattern.searchRepository(true);
					} else {
						pattern.searchRepository(false);
					}
				}

				private void searchRepository(final RepositorySearchPatter pattern) {
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {
							BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), new Runnable() {

								@Override
								public void run() {
									RepositoryController rc = OtmRegistry.getMainController().getRepositoryController();
									List<RepositoryItem> filteredRepositories = rc.search(getFilterString());
									pattern.setVisibleRepositories(filteredRepositories);
								}
							});
						}
					});
				}

			});
			return refreshJob;
		}

		@Override
		protected void textChanged() {
			super.textChanged();
		}

	}

	/**
	 * {@link FilteredTree} wants {@link ILabelProvider} so this class simple adapter to enable
	 * {@link DecoratingStyledCellLabelProvider}
	 * 
	 */
	class SupportTreeFilterProvider extends DecoratingStyledCellLabelProvider implements ILabelProvider {

		public SupportTreeFilterProvider(IStyledLabelProvider labelProvider, ILabelDecorator decorator,
				IDecorationContext decorationContext) {
			super(labelProvider, decorator, decorationContext);
		}

		@Override
		public String getText(Object element) {
			return getStyledText(element).getString();
		}

	}

	public RepositoryMenus(final Composite parent) {
		filteredTree = new SearchRepositoryTree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
				new RepositorySearchPatter(), true);
		viewer = filteredTree.getViewer();

		viewer.setContentProvider(new RepositoryTreeContentProvider());
		IWorkbench workbench = PlatformUI.getWorkbench();
		DecoratingStyledCellLabelProvider decorator = new SupportTreeFilterProvider(new RepositoryTreeLabelProvider(),
				workbench.getDecoratorManager(), null);
		viewer.setLabelProvider(decorator);
		// viewer.setLabelProvider(new RepositoryTreeLabelProvider2());
		// 8/1/2015 dmh - sorter and comparator must be set in RepositoryView
		// viewer.setSorter(new RepositoryTreeSorter());
		viewer.setComparator(new RepositoryTreeComparator());

		menuManager = new MenuManager();
		final Menu menu = menuManager.createContextMenu(viewer.getControl());

		// final MenuManager repositoryMenu = new MenuManager("Repository", "Repository_Menu_ID");
		final MenuManager addToProjectMenu = new MenuManager("Add to Project", "Repository_AddToProject_Menu_ID");
		final Action AddToProjectAction = new AddToProjectAction();
		final Action SyncRepoAction = new SyncRepositoryAction();
		addToProjectMenu.add(AddToProjectAction);

		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(final IMenuManager manager) {

				addToProjectMenu.removeAll();
				manager.add(SyncRepoAction);

				if (viewer.getSelection().isEmpty()) {
					//
				} else if (viewer.getSelection() instanceof IStructuredSelection) {
					final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

					final Object selected = selection.getFirstElement();
					if (!(selected instanceof Node)) {
						return;
					}

					final Node node = (Node) selected;

					if (node instanceof RepositoryItemNode) {
						final List<Action> importActions = createAddActionsForItems(node);
						for (final Action libAction : importActions) {
							addToProjectMenu.add(libAction);
						}
						manager.add(addToProjectMenu);
						// Need to link repository items to project items before these will work
						// manager.add(new Separator());
						// manager.add(commitLibraryAction);
						// manager.add(lockLibraryAction);
						// manager.add(revertLibraryAction);
						// manager.add(unlockLibraryAction);

					}
					manager.updateAll(true);
				}
			}

			private List<Action> createAddActionsForItems(final Node context) {
				final List<Action> itemActions = new ArrayList<Action>();
				ProjectController pc = OtmRegistry.getMainController().getProjectController();
				for (ProjectNode pn : pc.getAll()) {
					if (pn.isBuiltIn())
						continue;
					final StringProperties sp = new DefaultStringProperties();
					sp.set(PropertyType.TEXT, pn.getName());
					itemActions.add(new AddToProjectAction(sp, pn));
				}
				return itemActions;
			}
		});
		menuManager.setRemoveAllWhenShown(true);
		viewer.getControl().setMenu(menu);
	}

	public RepositoryMenus(Composite parent, IWorkbenchPartSite site) {
		this(parent);
		site.registerContextMenu(menuManager, viewer);
	}

	public void selectNode(INode n) {
		if (n == null) {
			viewer.setSelection(null);
		} else {
			if (viewer.getSelection() != n)
				viewer.setSelection(new StructuredSelection(n), true);
		}
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public boolean isIncludeRepositorySearch() {
		return includeRepositorySearch;
	}

	public void setIncludeRepositorySearch(boolean includeRepositorySearch) {
		this.includeRepositorySearch = includeRepositorySearch;
		refilter();
	}

	private void refilter() {
		filteredTree.textChanged();
		viewer.refresh();
	}

}
