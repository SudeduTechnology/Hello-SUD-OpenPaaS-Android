package global.sud.gi.SUDGIWrapper.custom.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

public class LocalizationResourcesTest {

    private static final String REQUIRED_RESOURCE_PREFIX = "sud_op_wck_";
    private static final List<String> REQUIRED_VALUE_DIRECTORIES = Arrays.asList(
            "values",
            "values-zh",
            "values-in",
            "values-pt",
            "values-th",
            "values-vi",
            "values-ru"
    );

    @Test
    public void allRequiredLanguagesContainTheSameNonEmptyStringResources() throws Exception {
        File resourceRoot = findResourceRoot();
        Map<String, String> defaultStrings = readStrings(new File(resourceRoot, "values/strings.xml"));
        assertFalse("Default string resources must not be empty", defaultStrings.isEmpty());

        for (String directory : REQUIRED_VALUE_DIRECTORIES) {
            File stringsFile = new File(resourceRoot, directory + "/strings.xml");
            assertTrue("Missing required language file: " + stringsFile, stringsFile.isFile());

            Map<String, String> localizedStrings = readStrings(stringsFile);
            assertEquals(directory + " must define exactly the same keys as values", defaultStrings.keySet(), localizedStrings.keySet());
            for (Map.Entry<String, String> entry : localizedStrings.entrySet()) {
                assertTrue(directory + "/" + entry.getKey() + " must use the module resource prefix", entry.getKey().startsWith(REQUIRED_RESOURCE_PREFIX));
                assertFalse(directory + "/" + entry.getKey() + " must not be empty", entry.getValue().trim().isEmpty());
            }
        }
    }

    @Test
    public void allLayoutResourcesUseTheModulePrefix() {
        File resourceRoot = findResourceRoot();
        File[] resourceDirectories = resourceRoot.listFiles();
        assertNotNull("Cannot list resource directories under " + resourceRoot, resourceDirectories);

        boolean foundLayout = false;
        for (File resourceDirectory : resourceDirectories) {
            if (!resourceDirectory.isDirectory() || !resourceDirectory.getName().startsWith("layout")) {
                continue;
            }
            File[] layoutFiles = resourceDirectory.listFiles();
            assertNotNull("Cannot list layout resources under " + resourceDirectory, layoutFiles);
            for (File layoutFile : layoutFiles) {
                if (!layoutFile.isFile() || !layoutFile.getName().endsWith(".xml")) {
                    continue;
                }
                foundLayout = true;
                assertTrue(layoutFile + " must use the module resource prefix", layoutFile.getName().startsWith(REQUIRED_RESOURCE_PREFIX));
            }
        }
        assertTrue("At least one layout resource must be present", foundLayout);
    }

    private File findResourceRoot() {
        File workingDirectory = new File(System.getProperty("user.dir"));
        File moduleResourceRoot = new File(workingDirectory, "src/main/res");
        if (moduleResourceRoot.isDirectory()) {
            return moduleResourceRoot;
        }

        File repositoryResourceRoot = new File(workingDirectory, "SUDGIWrapper/src/main/res");
        if (repositoryResourceRoot.isDirectory()) {
            return repositoryResourceRoot;
        }
        throw new AssertionError("Cannot locate SUDGIWrapper/src/main/res from " + workingDirectory.getAbsolutePath());
    }

    private Map<String, String> readStrings(File stringsFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document document = factory.newDocumentBuilder().parse(stringsFile);
        NodeList nodes = document.getDocumentElement().getChildNodes();
        Map<String, String> strings = new LinkedHashMap<>();

        for (int index = 0; index < nodes.getLength(); index++) {
            Node node = nodes.item(index);
            if (node.getNodeType() != Node.ELEMENT_NODE || !"string".equals(node.getNodeName())) {
                continue;
            }
            Element element = (Element) node;
            strings.put(element.getAttribute("name"), element.getTextContent());
        }
        return strings;
    }
}
