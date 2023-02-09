package com.hummer.common.core.proxy.vsphere;

import com.hummerrisk.commons.exception.PluginException;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class VsphereClient {
    private static Logger logger = LoggerFactory.getLogger(VsphereClient.class);


    private static final int connectionTimeout = 60000;
    private static final int readTimeout = 120000;

    private ServiceInstance si;
    private ManagedEntity rootEntity;

    private static final String CLUSTER_TYPE_NAME = "ClusterComputeResource";

    private String vCenterVersion;
    private ManagedEntity mainEntity;
    private String vCenterIp;
    private String dataCenterId;

    public VsphereClient(String vcenterIp, String userName, String password) throws PluginException {
        this(vcenterIp, userName, password, null);
    }

    public VsphereClient(String vcenterIp, String userName, String password,
                         String dataCenterId) throws PluginException {
        try {
            synchronized (CLUSTER_TYPE_NAME) {
                si = new ServiceInstance(new URL("https://" + vcenterIp + "/sdk"), userName, password, true, connectionTimeout, readTimeout);
                this.vCenterVersion = si.getAboutInfo().getVersion();
                this.rootEntity = si.getRootFolder();
                this.mainEntity = si.getRootFolder();
                this.vCenterIp = vcenterIp;
            }
        } catch (RemoteException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            if (e.toString().contains("InvalidLogin")) {
                throw new PluginException("Account verification failed!", e);
            }
            throw new PluginException("Failed to connect to Vsphere server!", e);
        } catch (MalformedURLException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new PluginException("Vsphere server address error!", e);
        }
        updateRootEntity(dataCenterId);
    }

    public void closeConnection() {
        si.getServerConnection().logout();
    }

    private void updateRootEntity(String dataCenterId) throws PluginException {
        if (dataCenterId != null && dataCenterId.trim().length() > 0) {
            List<Datacenter> dcList = listDataCenters();
            for (Datacenter dc : dcList) {
                if (dc.getName().equals(dataCenterId.trim())) {
                    rootEntity = dc;
                    this.dataCenterId = dataCenterId.trim();
                    break;
                }
            }
        }
    }

    public List<Datacenter> listDataCenters() throws PluginException {
        return listResources(Datacenter.class);
    }

    private <T extends ManagedEntity> List<T> listResources(Class<T> resClass) throws PluginException {
        return listResources(resClass, rootEntity);
    }

    @SuppressWarnings("unchecked")
    private <T extends ManagedEntity> List<T> listResources(Class<T> resClass, ManagedEntity rootEntity) throws PluginException {
        List<T> list = new ArrayList<T>();
        try {
            ManagedEntity[] mes = new InventoryNavigator(rootEntity).searchManagedEntities(resClass.getSimpleName());
            if (mes != null) {
                for (ManagedEntity m : mes) {
                    list.add((T) m);
                }
            }
        } catch (InvalidProperty e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new PluginException("invalid parameters!" + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new PluginException("Error getting resources!" + e.getLocalizedMessage(), e);
        }
        return list;
    }

    public boolean isUseCustomSpec() {
        return version() >= 550 ;
    }

    public double version() {
        String[] split = StringUtils.split(vCenterVersion, ".");
        ArrayUtils.reverse(split);
        double versionNum = 0;
        for (int i = 0, length = split.length; i < length; i++) {
            versionNum += Double.valueOf(split[i]) * Math.pow(10, i);
        }
        return versionNum;
    }

}
