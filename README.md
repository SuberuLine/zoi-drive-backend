## Introduction

---

## Installation

---

### Aria2 Pro

项目地址：https://github.com/P3TERX/Aria2-Pro-Docker?tab=readme-ov-file

使用Aria2 Pro处理磁力下载，方便docker部署，也可替换为使用Aria2

注意自行替换RPC_SECRET

```bash
docker run -d \
    --name aria2-pro \
    --log-opt max-size=1m \
    -e PUID=0 \
    -e PGID=0 \
    -e RPC_SECRET=zoidrive \
    -e RPC_PORT=6800 \
    -p 6800:6800 \
    -e LISTEN_PORT=6888 \
    -p 6888:6888 \
    -p 6888:6888/udp \
    -v ~/aria2-config:/config \
    -v ~/aria2-downloads:/downloads \
    p3terx/aria2-pro
```

