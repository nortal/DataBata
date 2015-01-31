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
package eu.databata.engine.rewriterule;

import java.util.List;

/**
 * @author Aleksei Lissitsin {@literal <aleksei.lissitsin@webmedia.ee>}
 */
public class ChainedRewriteRule implements RewriteRule {
  private List<RewriteRule> rules;

  public void setRules(List<RewriteRule> rules) {
    this.rules = rules;
  }

  public String apply(String s) {
    if (rules == null) {
      return s;
    }

    for (RewriteRule rule : rules) {
      s = rule.apply(s);
    }

    return s;
  }

}
