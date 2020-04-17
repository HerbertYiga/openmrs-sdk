package org.openmrs.maven.plugins;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openmrs.maven.plugins.model.Server;
import org.openmrs.maven.plugins.utility.Project;

import java.io.File;


/**
*
* @goal unwatch
* @requiresProject false
*
*/
public class Unwatch extends AbstractTask {
	
	/**
     * @parameter  property="serverId"
     */
    private String serverId;
    
    /**
     * @parameter  property="artifactId"
     */
    private String artifactId;

    /**
     * @parameter  property="groupId"
     */
    private String groupId;

	@Override
    public void executeTask() throws MojoExecutionException, MojoFailureException {
	    serverId = wizard.promptForExistingServerIdIfMissing(serverId);
	    Server serverConfig = loadValidatedServer(serverId);
	    
	    File userDir = new File(System.getProperty("user.dir"));
	    if (StringUtils.isBlank(artifactId) && Project.hasProject(userDir)) {
	    	Project project = Project.loadProject(userDir);
            if (serverConfig.removeWatchedProjectByExample(new Project(project.getGroupId(), project.getArtifactId(), null, null)) != null) {
            	serverConfig.save();
				wizard.showMessage("Stopped watching " + project.getPath() + " for changes.");
            } else {
				wizard.showMessage(project.getArtifactId() + " has not been watched.");
            }
        } else {
            artifactId = wizard.promptForValueIfMissing(artifactId, "artifactId");

            if (artifactId.equals("*")) {
        		serverConfig.clearWatchedProjects();
        		serverConfig.save();
        		wizard.showMessage("Stopped watching all projects for changes.");
        	} else {
	        	Project project = new Project(groupId, artifactId, null, null);
	        	project = serverConfig.removeWatchedProjectByExample(project);
	        	if (project != null) {
	        		serverConfig.save();
	        		wizard.showMessage("Stopped watching " + project.getPath() + " for changes.");
	        	} else {
	        		wizard.showMessage((groupId != null) ? (groupId + ":") : "" + artifactId + " has not been watched.");
	        	}
        	}
        }
	   
    }
	
}
