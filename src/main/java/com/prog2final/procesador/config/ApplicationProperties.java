package com.prog2final.procesador.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Prog II Final.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private GeneratorProperties generator;
    private CompServicesProperties compServices;

    public ApplicationProperties() {}

    public ApplicationProperties(GeneratorProperties generator, CompServicesProperties compServices) {
        this.generator = generator;
        this.compServices = compServices;
    }

    public GeneratorProperties getGenerator() {
        return generator;
    }

    public void setGenerator(GeneratorProperties generator) {
        this.generator = generator;
    }

    public CompServicesProperties getCompServices() {
        return compServices;
    }

    public void setCompServices(CompServicesProperties compServices) {
        this.compServices = compServices;
    }

    public static class GeneratorProperties {

        private String url;
        private String token;
        private String ordersEndpoint;

        public GeneratorProperties() {}

        public GeneratorProperties(String url, String token, String ordersEndpoint) {
            this.url = url;
            this.token = token;
            this.ordersEndpoint = ordersEndpoint;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getOrdersEndpoint() {
            return ordersEndpoint;
        }

        public void setOrdersEndpoint(String ordersEndpoint) {
            this.ordersEndpoint = ordersEndpoint;
        }
    }

    public static class CompServicesProperties {

        private String url;
        private String token;
        private String stocksEndpoint;
        private String clientsEndpoint;
        private ReportsEndpointsProperties reportsEndpoints;
        private String clientStockEndpoint;

        public CompServicesProperties() {}

        public CompServicesProperties(
            String url,
            String token,
            String stocksEndpoint,
            String clientsEndpoint,
            ReportsEndpointsProperties reportsEndpoints,
            String clientStockEndpoint
        ) {
            this.url = url;
            this.token = token;
            this.stocksEndpoint = stocksEndpoint;
            this.clientsEndpoint = clientsEndpoint;
            this.reportsEndpoints = reportsEndpoints;
            this.clientStockEndpoint = clientStockEndpoint;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getStocksEndpoint() {
            return stocksEndpoint;
        }

        public void setStocksEndpoint(String stocksEndpoint) {
            this.stocksEndpoint = stocksEndpoint;
        }

        public String getClientsEndpoint() {
            return clientsEndpoint;
        }

        public void setClientsEndpoint(String clientsEndpoint) {
            this.clientsEndpoint = clientsEndpoint;
        }

        public ReportsEndpointsProperties getReportsEndpoints() {
            return reportsEndpoints;
        }

        public void setReportsEndpoints(ReportsEndpointsProperties reportsEndpoints) {
            this.reportsEndpoints = reportsEndpoints;
        }

        public String getClientStockEndpoint() {
            return clientStockEndpoint;
        }

        public void setClientStockEndpoint(String clientStockEndpoint) {
            this.clientStockEndpoint = clientStockEndpoint;
        }

        public static class ReportsEndpointsProperties {

            private String reportsEndpoint;
            private String queryEndpoint;

            public ReportsEndpointsProperties() {}

            public ReportsEndpointsProperties(String reportsEndpoint, String queryEndpoint) {
                this.reportsEndpoint = reportsEndpoint;
                this.queryEndpoint = queryEndpoint;
            }

            public String getReportsEndpoint() {
                return reportsEndpoint;
            }

            public void setReportsEndpoint(String reportsEndpoint) {
                this.reportsEndpoint = reportsEndpoint;
            }

            public String getQueryEndpoint() {
                return queryEndpoint;
            }

            public void setQueryEndpoint(String queryEndpoint) {
                this.queryEndpoint = queryEndpoint;
            }
        }
    }

    @Override
    public String toString() {
        return "ApplicationProperties{" + "generator=" + generator + ", compServices=" + compServices + '}';
    }
}
