package com.manav.dockapimainserver;

import lombok.Data;

@Data
public class ProjectRequestBody {
   private String projectName;
   private String githubUrl;

   public String getProjectName() {
      return projectName;
   }

   public void setProjectName(String projectName) {
      this.projectName = projectName;
   }

   public String getGithubUrl() {
      return githubUrl;
   }

   public void setGithubUrl(String githubUrl) {
      this.githubUrl = githubUrl;
   }
}
