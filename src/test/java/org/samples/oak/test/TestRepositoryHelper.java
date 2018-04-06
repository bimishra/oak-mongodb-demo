package org.samples.oak.test;

import org.junit.Before;
import org.junit.Test;
import org.samples.oak.demo.bean.AssetDetail;
import org.samples.oak.demo.bean.FileResponse;
import org.samples.oak.demo.bean.VersionHistory;
import org.samples.oak.demo.service.RepoHistoryHelper;
import org.samples.oak.demo.service.RepositoryBuilder;
import org.samples.oak.demo.service.RepositoryHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

public class TestRepositoryHelper {
    private static Repository repo;

    @Test
    public void shouldEditFile() {
        try {
            Session session = getSession();
            RepositoryHelper.editFile(session, "/testNode/deal/subdeal", new File("ChildClass.java"), "bishnu");
            System.out.println("File Edit Complete");
            cleanUp(session); // do this in finally
        } catch (RepositoryException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetAssets() {
        String basePath = "/testNode/deal/subdeal";
        try {
            Session session = getSession();
            System.out.println("Starting the asset fetch...");
            List<AssetDetail> assets = RepositoryHelper.getAssets(session, basePath);
            System.out.println(assets.size());
            for (AssetDetail assetDetail : assets) {
                System.out.println(assetDetail);
            }
            cleanUp(session); // do this in finally
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetVersionHistory() {
        try {
            Session session = getSession();
            List<VersionHistory> versions = RepoHistoryHelper.getVersionHistory(session, "/testNode/deal/subdeal",
                    "ChildClass.java");
            for (VersionHistory versionHistory : versions) {
                System.out.println(versionHistory);
            }
            cleanUp(session); // do this in finally
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRestoreVersion() {
        try {
            Session session = getSession();
            RepoHistoryHelper.restoreVersion(session, "56d9b193-7a4b-4c69-88db-20d29633d822");
            cleanUp(session); // do this in finally
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldDeleteNode() {
        try {
            Session session = getSession();
            RepoHistoryHelper.deleteVersionHistories(session, "/testNode/deal/subdeal", "ChildClass.java");
            RepositoryHelper.deleteNode(session, "/testNode/deal/subdeal", "ChildClass.java");
            cleanUp(session); // do this in finally
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFileContent() {
        try {
            Session session = getSession();
            System.out.println("Fething the file...");
            FileResponse fileResponse = RepositoryHelper.getFileContents(session, "/testNode/deal/subdeal",
                    "ChildClass.java");
            byte[] content = fileResponse.getBytes();
            if (content != null && content.length > 0) {
                FileOutputStream fos = new FileOutputStream("E:/OutFiles/" + "ChildClass_out1.java");
                fos.write(content);
                fos.close();
                System.out.println("File fetch complete...");
            }
            cleanUp(session); // do this in finally
        } catch (RepositoryException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void shoudAddFile() {
        try {
            System.out.println("Adding the file...");
            Session session = getSession();

            RepositoryHelper.addFileNode(session, "/testNode/deal/subdeal", new File("ChildClass.java"), "admin");

            System.out.println("Files added...");
            cleanUp(session); // do this in finally
        } catch (RepositoryException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldCreateNodes() {
        try {
            Session session = getSession();
            RepositoryHelper.createNodes(session, "/testNode/deal3");
            System.out.println("Node created...");
            cleanUp(session); // do this in finally
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Session getSession() throws LoginException, RepositoryException {
        if (repo != null)
            return repo.login(new SimpleCredentials("admin", "admin".toCharArray()));
        else
            throw new NullPointerException("Repository not initialized");
    }

    @Before
    public void doSetup() {
        try {
            repo = RepositoryBuilder.getRepo("localhost", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void cleanUp(Session session) {
        if (session != null) {
            session.logout();
        }
    }
}
