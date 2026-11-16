package lt.pcbuilder.domain;

import java.util.ArrayList;
import java.util.List;


public final class DemoCatalog {
    private DemoCatalog() { }
    private static Part part(String tenant, String id, String name, PartType type, int euro, int perf,
                             String socket, String ram, int draw, int power, String form,
                             int length, int maxLength, String url) {
        return new Part(id, tenant, name, type, euro, perf, socket, ram, draw, power, form, length, maxLength, url);
    }
    public static List<Part> forTenant(String tenant) {
        List<Part> result = new ArrayList<>();
        String cpus = "https://www.skytech.lt/procesoriai-stac-komp-procesoriai-desktop-cpu-c-86_85_826_182_584.html?grp=1&pagesize=100&sand=1&sort=5a";
        String am4 = "https://www.skytech.lt/pagrindines-plokstes-intel-procesoriams-c-86_85_84_248.html?filter%5B0%5D=gamintojas&filter%5B1%5D=params&filter%5B2%5D=kaina&fkaina%5B248%5D%5Bi%5D=&fkaina%5B248%5D%5Bn%5D=&fparams%5B248%5D%5B0%5D=1268&fparams%5B248%5D%5B1%5D=1273&grp=1&page=3&pagesize=100&pav=0&sand=0&sort=3a";
        String a520 = "https://www.skytech.lt/pagrindines-plokstes-priedai-pagrindines-plokstes-c-86_85_826_248.html?f=g%28%29%2Cp%28511%2C505%2C504%2C506%2C1196%2C507%2C993%2C534%29%2Ck%28149%2C386%29&grp=1&page=3&pagesize=100&pav=0&sand=0&sort=5a&tab=2";
        String team = "https://www.kaina24.lt/p/team-group-elite-ted416g3200c2201-cl22-16gb-ddr4-3200-mhz-udimm/";
        String fury = "https://dealai.lt/prekes/kingston-fury-beast-kf432c16bb1-16-cl16-16gb-ddr4-3200-mhz-dimm-kingston-fury-beast-16gb-ddr4-3200mhz-cl16-juoda-kf432c16bb1-16";
        String rtx3050 = "https://www.kaina24.lt/s/palit-stormx/";
        String rtx5060 = "https://www.kaina24.lt/p/gigabyte-geforce-rtx-5060-windforce-oc-8gb-ddr7/";
        String rtx5050 = "https://dealai.lt/prekes/msi-geforce-rtx-5050-gaming-oc-8gb-ddr6-msi-geforce-rtx-5050-gaming-oc-8gb-gddr6";
        String nv500 = "https://www.kaina24.lt/s/nv3-500gb/";
        String nv1000 = "https://www.kaina24.lt/s/kingston-nv3-1-tb/";
        String cx550 = "https://www.kaina24.lt/s/cx550/";
        String case40 = "https://www.kaina24.lt/s/deepcool-matrexx-40-3fs/";
        if ("pcstart".equals(tenant)) {
            result.add(part(tenant,"a-cpu-5500","AMD Ryzen 5 5500 BOX (su aušintuvu)",PartType.CPU,107,70,"AM4",null,65,0,null,0,0,cpus));
            result.add(part(tenant,"a-cpu-5600","AMD Ryzen 5 5600 MULTIPACK (su aušintuvu)",PartType.CPU,120,84,"AM4",null,65,0,null,0,0,cpus));
            result.add(part(tenant,"a-mb-a520","Gigabyte A520M DS3H V2",PartType.MOTHERBOARD,60,0,"AM4","DDR4",35,0,"mATX",0,0,a520));
            result.add(part(tenant,"a-mb-b550","Gigabyte B550M DS3H R2",PartType.MOTHERBOARD,95,0,"AM4","DDR4",35,0,"mATX",0,0,am4));
            result.add(part(tenant,"a-ram-team","TeamGroup Elite 16GB DDR4-3200 (TED416G3200C2201)",PartType.RAM,127,70,null,"DDR4",8,0,null,0,0,team));
            result.add(part(tenant,"a-ram-kingston","Kingston FURY Beast 16GB DDR4-3200 (KF432C16BB1/16)",PartType.RAM,187,75,null,"DDR4",8,0,null,0,0,fury));
            result.add(part(tenant,"a-gpu-entry","Palit GeForce RTX 3050 StormX 6GB",PartType.GPU,285,55,null,null,70,0,null,162,0,rtx3050));
            result.add(part(tenant,"a-gpu-mid","Gigabyte GeForce RTX 5060 WINDFORCE OC 8GB",PartType.GPU,429,88,null,null,145,0,null,199,0,rtx5060));
            result.add(part(tenant,"a-gpu-5050","MSI GeForce RTX 5050 GAMING OC 8GB",PartType.GPU,527,71,null,null,130,0,null,230,0,rtx5050));
            result.add(part(tenant,"a-ssd-500","Kingston NV3 500GB M.2 NVMe",PartType.STORAGE,95,60,null,null,5,0,null,0,0,nv500));
            result.add(part(tenant,"a-ssd-1000","Kingston NV3 1TB M.2 NVMe",PartType.STORAGE,130,78,null,null,5,0,null,0,0,nv1000));
            result.add(part(tenant,"a-psu-550","Corsair CX550 550W 80+ Bronze",PartType.PSU,57,0,null,null,0,550,null,0,0,cx550));
            result.add(part(tenant,"a-case-matrexx","DeepCool MATREXX 40 3FS mATX",PartType.CASE,42,0,null,null,0,0,"mATX",0,320,case40));
        } else if ("buildlab".equals(tenant)) {
            result.add(part(tenant,"b-cpu-basic","AMD Ryzen 5 5500 BOX (su aušintuvu)",PartType.CPU,107,70,"AM4",null,65,0,null,0,0,cpus));
            result.add(part(tenant,"b-cpu-plus","AMD Ryzen 5 5600 MULTIPACK (su aušintuvu)",PartType.CPU,120,84,"AM4",null,65,0,null,0,0,cpus));
            result.add(part(tenant,"b-mb","Gigabyte B550M DS3H R2",PartType.MOTHERBOARD,95,0,"AM4","DDR4",35,0,"mATX",0,0,am4));
            result.add(part(tenant,"b-ram","Kingston FURY Beast 16GB DDR4-3200 (KF432C16BB1/16)",PartType.RAM,187,75,null,"DDR4",8,0,null,0,0,fury));
            result.add(part(tenant,"b-gpu-basic","Palit GeForce RTX 3050 StormX 6GB",PartType.GPU,285,55,null,null,70,0,null,162,0,rtx3050));
            result.add(part(tenant,"b-gpu-fast","Gigabyte GeForce RTX 5060 WINDFORCE OC 8GB",PartType.GPU,429,88,null,null,145,0,null,199,0,rtx5060));
            result.add(part(tenant,"b-ssd","Kingston NV3 500GB M.2 NVMe",PartType.STORAGE,95,60,null,null,5,0,null,0,0,nv500));
            result.add(part(tenant,"b-psu","Corsair CX550 550W 80+ Bronze",PartType.PSU,57,0,null,null,0,550,null,0,0,cx550));
            result.add(part(tenant,"b-case","DeepCool MATREXX 40 3FS mATX",PartType.CASE,42,0,null,null,0,0,"mATX",0,320,case40));
        } else throw new IllegalArgumentException("Unknown tenant: " + tenant);
        return List.copyOf(result);
    }
}
