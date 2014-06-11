package eu.databata.engine.rewriterule;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
public class ReplaceRewriteRule implements RewriteRule, Serializable {

  private Pattern pattern;

  private String replacement;

  public void setPattern(String pattern) {
    if (pattern != null && !pattern.trim().equals("")) {
      this.pattern = Pattern.compile(pattern);
    }
  }

  public void setReplacement(String replacement) {
    this.replacement = replacement;
  }

  public String apply(String s) {
    if (pattern != null && replacement != null) {
      return pattern.matcher(s).replaceAll(replacement);
    }
    return s;
  }

}
