terraform {
  required_providers {
    yandex = {
      source  = "yandex-cloud/yandex"
      version = "~> 0.136"
    }
  }
}

provider "yandex" {
  cloud_id  = "b1ge1g8ik86mantc6hu4"
  folder_id = "b1g7jmcr38kf4en666g0"
  service_account_key_file = "authorized_key.json"
}

