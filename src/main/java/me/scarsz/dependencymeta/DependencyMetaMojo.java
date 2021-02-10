package me.scarsz.dependencymeta;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mojo(
        name = "dependencymeta",
        defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
        requiresDependencyCollection = ResolutionScope.RUNTIME,
        requiresDependencyResolution = ResolutionScope.RUNTIME
)
public class DependencyMetaMojo extends AbstractMojo {

    @Parameter(
            defaultValue = "${project}",
            readonly = true,
            required = true
    )
    private MavenProject project;

    @Parameter(
            property = "dependencyFile",
            defaultValue = "${project.build.directory}/classes/META-INF/${project.artifactId}.dependencies",
            required = true
    )
    private File dependencyFile;

    @Parameter(
            property = "transitive",
            defaultValue = "true",
            required = true
    )
    private boolean transitive;

//    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
//    private RepositorySystemSession session;

    public void execute() throws MojoExecutionException {
        Map<Artifact, String> dependencies = new HashMap<>();

        //noinspection deprecation
        for (Artifact artifact : transitive ? project.getArtifacts() : project.getDependencyArtifacts()) {
            System.out.println(artifact.getGroupId() + ":" + artifact.getId() + ":" + artifact.getVersion());

            String hash = null;
            File hashFile = new File(artifact.getFile().getAbsolutePath() + ".sha1");
            if (hashFile.exists()) {
                try {
                    hash = FileUtils.readFileToString(hashFile, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    System.err.println("Failed to read SHA1 for " + artifact + ": " + e.getMessage());
                }
            }

            if (hash == null) {
                try {
                    hash = DigestUtils.sha1Hex(FileUtils.readFileToByteArray(artifact.getFile()));
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to calculate SHA1 for artifact " + artifact, e);
                }
            }

            dependencies.put(artifact, hash);
        }

        try {
            FileUtils.writeStringToFile(
                    dependencyFile,
                    dependencies.entrySet().stream()
                            .map(entry -> entry.getKey() + " " + entry.getValue())
                            .collect(Collectors.joining("\n")),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to write dependencies to " + dependencyFile.getPath(), e);
        }
    }

}
