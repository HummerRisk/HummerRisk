package com.hummerrisk.proxy.azure;

import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.Location;
import com.microsoft.azure.management.resources.Subscription;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.hummerrisk.base.domain.Proxy;
import okhttp3.JavaNetAuthenticator;
import org.apache.ibatis.plugin.PluginException;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.util.*;
import java.util.concurrent.*;


public class AzureClient {
    private Azure azure;

    public static List<Map<String, String>> regions = new ArrayList<>();

    public static Map<String, String> map = new HashMap<>();

    public AzureClient() {

    }

    public AzureClient(final AzureCredential azureCredential, Proxy proxy) throws PluginException {
        ConcurrentHashMap<String, Azure> azureConcurrentHashMap = new ConcurrentHashMap<String, Azure>();

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            List<Callable<Azure>> callableList = new ArrayList<Callable<Azure>>();
            callableList.add(new Callable<Azure>() {
                public Azure call() throws Exception {
                    ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(azureCredential.getClient(), azureCredential.getTenant(), azureCredential.getKey(), AzureEnvironment.AZURE_CHINA);
                    final AzureProxySetting proxySetting = ProxyUtils.getProxySetting(proxy);
                    Azure.Configurable configurable = Azure.configure();
                    Azure azureChina = null;
                    if (proxySetting != null) {
                        java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxySetting.getHost(), proxySetting.getPort()));
                        configurable.withProxy(proxy);
                        if (proxySetting.getUserName() != null && proxySetting.getUserName().trim().length() > 0) {
                            Authenticator.setDefault(new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(proxySetting.getUserName(), proxySetting.getPassword()
                                            .toCharArray());
                                }
                            });
                            configurable.withProxyAuthenticator(new JavaNetAuthenticator());
                        }

                    }
                    azureChina = configurable.authenticate(credentials).withSubscription(azureCredential.getSubscription());
                    azureChina.resourceGroups().list();
                    map.put("regionId", "AzureChinaCloud");
                    map.put("regionName", "中国区");
                    regions.add(map);
                    return azureChina;
                }
            });
            callableList.add(new Callable<Azure>() {
                public Azure call() throws Exception {
                    ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(azureCredential.getClient(), azureCredential.getTenant(), azureCredential.getKey(), AzureEnvironment.AZURE);
                    final AzureProxySetting proxySetting = ProxyUtils.getProxySetting(proxy);
                    Azure.Configurable configurable = Azure.configure();
                    Azure azureGlobal = null;
                    if (proxySetting != null) {
                        java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxySetting.getHost(), proxySetting.getPort()));
                        configurable.withProxy(proxy);
                        if (proxySetting.getUserName() != null && proxySetting.getUserName().trim().length() > 0) {
                            Authenticator.setDefault(new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(proxySetting.getUserName(), proxySetting.getPassword()
                                            .toCharArray());
                                }
                            });
                            configurable.withProxyAuthenticator(new JavaNetAuthenticator());
                        }
                    }
                    azureGlobal = configurable.authenticate(credentials).withSubscription(azureCredential.getSubscription());
                    azureGlobal.resourceGroups().list();
                    map.put("regionId", "AzureCloud");
                    map.put("regionName", "国际区");
                    regions.add(map);
                    return azureGlobal;

                }
            });
            callableList.add(new Callable<Azure>() {
                public Azure call() throws Exception {
                    ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(azureCredential.getClient(), azureCredential.getTenant(), azureCredential.getKey(), AzureEnvironment.AZURE_US_GOVERNMENT);
                    final AzureProxySetting proxySetting = ProxyUtils.getProxySetting(proxy);
                    Azure.Configurable configurable = Azure.configure();
                    Azure azureUS = null;
                    if (proxySetting != null) {
                        java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxySetting.getHost(), proxySetting.getPort()));
                        configurable.withProxy(proxy);
                        if (proxySetting.getUserName() != null && proxySetting.getUserName().trim().length() > 0) {
                            Authenticator.setDefault(new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(proxySetting.getUserName(), proxySetting.getPassword()
                                            .toCharArray());
                                }
                            });
                            configurable.withProxyAuthenticator(new JavaNetAuthenticator());
                        }
                    }
                    azureUS = configurable.authenticate(credentials).withSubscription(azureCredential.getSubscription());
                    azureUS.resourceGroups().list();
                    map.put("regionId", "AzureUSGov");
                    map.put("regionName", "美国区");
                    regions.add(map);
                    return azureUS;

                }
            });
            callableList.add(new Callable<Azure>() {
                public Azure call() throws Exception {
                    ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(azureCredential.getClient(), azureCredential.getTenant(), azureCredential.getKey(), AzureEnvironment.AZURE_GERMANY);
                    final AzureProxySetting proxySetting = ProxyUtils.getProxySetting(proxy);
                    Azure.Configurable configurable = Azure.configure();
                    Azure azureGermany = null;
                    if (proxySetting != null) {
                        java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxySetting.getHost(), proxySetting.getPort()));
                        configurable.withProxy(proxy);
                        if (proxySetting.getUserName() != null && proxySetting.getUserName().trim().length() > 0) {
                            Authenticator.setDefault(new Authenticator() {
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(proxySetting.getUserName(), proxySetting.getPassword()
                                            .toCharArray());
                                }
                            });
                            configurable.withProxyAuthenticator(new JavaNetAuthenticator());
                        }
                    }
                    azureGermany = configurable.authenticate(credentials).withSubscription(azureCredential.getSubscription());
                    azureGermany.resourceGroups().list();
                    map.put("regionId", "AzureGermanyCloud");
                    map.put("regionName", "德国区");
                    regions.add(map);
                    return azureGermany;

                }
            });
            Exception exception = new Exception("Authentication Failed");
            try {
                List<Future<Azure>> futureList = executorService.invokeAll(callableList, 2, TimeUnit.MINUTES);
                for (int i = 0; i < futureList.size(); i++) {
                    try {
                        Future<Azure> future = futureList.get(i);
                        Azure authenticatedAzure = future.get();
                        if (authenticatedAzure != null) {
                            this.azure = authenticatedAzure;
                        }
                    } catch (Exception e) {
                        exception = e;
                    }
                }
            } finally {
                executorService.shutdown();
            }
            if (this.azure == null) {
                throw exception;
            }
            azureConcurrentHashMap.put(azureCredential.getSubscription(), this.azure);
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }

    //此region 不是chinanorth这种我们诠释的region，而是azure本身的中国区、国际区等概念
    public List<Map<String, String>> getCloudRegions () {
        return regions;
    }

    public List<AzureResourceObject> getRegions() {
        List<AzureResourceObject> result = new ArrayList<AzureResourceObject>();
        PagedList<Location> locationPagedList = azure.getCurrentSubscription().listLocations();
        locationPagedList.loadAll();
        for (Location location : locationPagedList) {
            AzureResourceObject azureRegion = new AzureResourceObject();
            azureRegion.setId(location.inner().name());
            azureRegion.setName(location.inner().displayName());
            result.add(azureRegion);
        }
        return result;
    }

    public Region getRegion(String regionId) {
        PagedList<Location> locationPagedList = azure.getCurrentSubscription().listLocations();
        locationPagedList.loadAll();
        for (Location location : locationPagedList) {
            if (location.region().name().equalsIgnoreCase(regionId)) {
                return location.region();
            }
        }
        return null;
    }

    public Subscription getCurrentSubscription() {
        return azure.subscriptions().getById(azure.subscriptionId());
    }

    /**
     * azure 所有区域
     */
    public final static List<String> getAzureResions() {
        return Arrays.asList("AzureCloud", "AzureChinaCloud", "AzureGermanyCloud", "AzureUSGov");
    }

}
