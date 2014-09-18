package eu.databata.gradle;
 
import org.gradle.api.*;

class DatabataGradlePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('dbt') << {
            println 'Databata plugin will be implemented soon...'
        }
    }
}
