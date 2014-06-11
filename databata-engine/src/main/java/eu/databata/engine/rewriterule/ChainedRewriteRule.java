package eu.databata.engine.rewriterule;

import java.util.List;

/**
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
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
