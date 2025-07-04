locals {
  services = {
    "quotation" = { 
      port = 8080, 
      cpu = 256, 
      memory = 512, 
      desired_count = 2, 
      container_port = 8080,
      path = "/api/quotes/*"
    },
    "payment" = { 
      port = 50051, 
      cpu = 512, 
      memory = 1024, 
      desired_count = 2, 
      container_port = 50051,
      path = "/api/payments/*"
    },
    "invoice" = { 
      port = 8082, 
      cpu = 512, 
      memory = 1024, 
      desired_count = 2, 
      container_port = 8080,
      path = "/api/invoices/*"
    }
  }
}
