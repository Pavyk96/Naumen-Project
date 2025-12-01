resource "yandex_compute_instance" "db" {
  name        = "project-db"
  platform_id = "standard-v3"
  zone        = "ru-central1-a"

  resources {
    cores  = 2
    memory = 2
  }

  boot_disk {
    initialize_params {
      image_id = "fd8qa5pcd0l7123dpvhc"
      size     = 20
    }
  }

  network_interface {
    subnet_id = yandex_vpc_subnet.public.id
    nat       = true
  }

  metadata = {
    user-data = <<-EOF
      #cloud-config
      packages:
        - docker.io

      write_files:
        - path: /usr/local/bin/start-project-db.sh
          permissions: '0755'
          content: |
            #!/bin/bash
            set -xe
            systemctl enable docker
            systemctl start docker
            docker run -d \
              --name project-db \
              -e POSTGRES_DB=project \
              -e POSTGRES_USER=project \
              -e POSTGRES_PASSWORD=project \
              -p 5432:5432 \
              postgres:16

      runcmd:
        - [ /usr/local/bin/start-project-db.sh ]
    EOF

    ssh-keys = "ubuntu:ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIOk2TiSsrmpBtgyXLiaGxDcZLVMpBNxrjmJFewGoJ7Hj pepethefrog27@mail.ru"
  }
}
