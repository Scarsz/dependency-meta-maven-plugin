# dependency-meta-maven-plugin
Maven plugin to write the module's dependencies to a resource

# Usage
```xml
<pluginRepositories>
    <pluginRepository>
        <id>scarsz</id>
        <url>https://nexus.scarsz.me/content/repositories/releases/</url>
    </pluginRepository>
</pluginRepositories>

<plugins>
    <plugin>
        <groupId>me.scarsz</groupId>
        <artifactId>dependency-meta-maven-plugin</artifactId>
        <version>1.0</version>
        <executions>
            <execution>
                <phase>prepare-package</phase>
                <goals>
                    <goal>dependencymeta</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```
