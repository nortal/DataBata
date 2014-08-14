/**
 *   Copyright 2014 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package eu.databata;

import eu.databata.engine.dao.PropagationDAO;
import eu.databata.engine.model.PropagationObject;
import eu.databata.engine.model.PropagationObject.ObjectType;
import eu.databata.engine.util.PropagationUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * This supplement propagator handles the propagation of views. In a view file it looks for references to other views in
 * the form "vsq_view_name" or "v_view_name", and attempts to process these first. As a convenience, it also removes
 * ending semicolons in case there are any.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 * @author Maksim Boiko
 */
public class ViewPropagation extends SupplementPropagation {

  private static final Logger LOG = Logger.getLogger(ViewPropagation.class);
  private final Pattern viewPattern = Pattern.compile("(vsq_\\w+|v_\\w+)(?: |;|,|$|\n|\r|\\))", Pattern.CASE_INSENSITIVE);

  public ViewPropagation(File directory, String moduleName, SQLPropagationTool sqlExecutor, PropagationDAO propagationDAO, String fileSearchRegexp) {
    super(directory, ObjectType.VIEW, moduleName, sqlExecutor, propagationDAO, fileSearchRegexp);
  }

  @Override
  protected void propagateObjects(List<PropagationObject> propagationObjects) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Sorting views according to dependencies");
    }
    List<PropagationObject> sortedViews = sortViews(propagationObjects);
    super.propagateObjects(sortedViews);
  };

  private List<PropagationObject> sortViews(Collection<PropagationObject> views) {
    Map<String, PropagationObject> allViews = new HashMap<String, PropagationObject>();
    for (PropagationObject view : views) {
      LOG.debug("Adding view <" + PropagationUtils.removeExtension(view.getPropagatedFile()).toUpperCase()
          + "> to map for sorting.");
      allViews.put(PropagationUtils.removeExtension(view.getPropagatedFile()).toUpperCase(), view);
    }

    List<PropagationObject> orderedViews = new ArrayList<PropagationObject>();
    for (PropagationObject view : views) {
      recViewAppend(view, allViews, orderedViews);
    }
    return orderedViews;
  }

  /**
   * Divide and conquer style recursive function, which allows a view to be added only after all of it's dependencies
   * have been met.
   */
  private void recViewAppend(PropagationObject viewFile,
                                    Map<String, PropagationObject> allViews,
                                    Collection<PropagationObject> orderedViewList) {
    if (!orderedViewList.contains(viewFile)) {
      Collection<PropagationObject> dependentOnViews = findDependentOnViews(viewFile, allViews);
      if (!dependentOnViews.isEmpty()) {
        LOG.debug("Depencencies for <" + viewFile.getObjectName() + "> is found.");
        for (PropagationObject dependentView : dependentOnViews) {
          LOG.debug("Searching dependencies for <" + dependentView.getObjectName() + ">.");
          recViewAppend(dependentView, allViews, orderedViewList);
        }
      }
      if (!orderedViewList.contains(viewFile)) {
        LOG.debug("Adding <" + viewFile.getObjectName() + "> to sorted list.");
        orderedViewList.add(viewFile);
      }
      allViews.remove(PropagationUtils.removeExtension(viewFile.getPropagatedFile()).toUpperCase());
    }
  }

  private Collection<PropagationObject> findDependentOnViews(PropagationObject viewFile,
                                                                    Map<String, PropagationObject> allViews) {
    Set<PropagationObject> deps = new HashSet<PropagationObject>();
    Matcher viewMatcher = viewPattern.matcher(PropagationUtils.readFile(viewFile.getPropagatedFile()));

    while (viewMatcher.find()) {
      String matchedName = viewMatcher.group(1).toUpperCase();
      LOG.debug("Matched group <" + viewMatcher.group() + ">");
      LOG.debug("Matched name found <" + matchedName + ">");
      PropagationObject foundFile = allViews.get(matchedName);
      if (foundFile == null) {
        String[] parts = matchedName.split("\\.");
        foundFile = allViews.get(parts[parts.length - 1]);
      }

      if (foundFile != null) {
        LOG.debug("Matched file found <" + foundFile.getObjectName() + ">");
        if (!foundFile.getObjectName().toUpperCase().equals(viewFile.getObjectName().toUpperCase()) && !deps.contains(foundFile)) {
          LOG.debug("Dependency match is found <" + foundFile.getObjectName() + ">");
          deps.add(foundFile);
        }
      }
    }
    return deps;
  }
}
