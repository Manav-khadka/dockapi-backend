package com.manav.dockapimainserver;

import lombok.Data;

@Data
public class ProjectRequestBody {
    String projectName;
    String githubUrl;
}
