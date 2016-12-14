SUMMARY = "A console-only image for the Lantronix pw2050 machine"

IMAGE_FEATURES += "splash"

LICENSE = "MIT"

inherit image

IMAGE_LINGUAS = ""

# main packages include binaries and other files for running
# -dev packages include .so and .h files
# -staticdev packages include .a and .h files

IMAGE_INSTALL += "avahi-daemon"
IMAGE_INSTALL += "bash"
IMAGE_INSTALL += "bcmdhd"
IMAGE_INSTALL += "bridge-utils"
IMAGE_INSTALL += "busybox"
IMAGE_INSTALL += "bzip2"
IMAGE_INSTALL += "cjson-dev"
IMAGE_INSTALL += "dbus"
IMAGE_INSTALL += "expat"
IMAGE_INSTALL += "ezxml-staticdev"
IMAGE_INSTALL += "fcgi"
IMAGE_INSTALL += "glib-2.0"
IMAGE_INSTALL += "gmp"
IMAGE_INSTALL += "gtest"
IMAGE_INSTALL += "i2c-tools"
IMAGE_INSTALL += "iproute2"
IMAGE_INSTALL += "iputils"
IMAGE_INSTALL += "kernel-modules"
IMAGE_INSTALL += "libbsd-mini-dev"
IMAGE_INSTALL += "libdaemon"
IMAGE_INSTALL += "libffi"
IMAGE_INSTALL += "libpcap"
IMAGE_INSTALL += "libpcre"
IMAGE_INSTALL += "libusb1"
IMAGE_INSTALL += "ltrx-apps"
IMAGE_INSTALL += "mii-diag"
IMAGE_INSTALL += "ncurses"
IMAGE_INSTALL += "openssl"
IMAGE_INSTALL += "readline"
IMAGE_INSTALL += "rng-tools"
IMAGE_INSTALL += "spawn-fcgi"
IMAGE_INSTALL += "tar"
IMAGE_INSTALL += "tcpdump"
IMAGE_INSTALL += "tzdata"
IMAGE_INSTALL += "u-boot-fw-utils"
IMAGE_INSTALL += "u-boot-mkimage"
IMAGE_INSTALL += "usbutils"
IMAGE_INSTALL += "util-linux"
IMAGE_INSTALL += "wide-dhcpv6"
IMAGE_INSTALL += "wpa-supplicant"
IMAGE_INSTALL += "zlib"

apply_overlay() {
    tar xvf ${THISDIR}/files/overlay.tar.xz -C ${IMAGE_ROOTFS}
    tar xvf ${THISDIR}/files/${MACHINE}-overlay.tar.xz -C ${IMAGE_ROOTFS}

    # un-expire account passwords
    sed -i 's/:0:0:99999/:10933:0:99999/' ${IMAGE_ROOTFS}/etc/shadow

    # override to use tmpfs instead of RAM disk
    install -D -m 0755 ${THISDIR}/files/ltrx_remount_etc ${IMAGE_ROOTFS}/sbin/ltrx_remount_etc

# add yocto tmpfs mounts to Lantronix fstab
    cat >> ${IMAGE_ROOTFS}/etc/fstab << EOF
tmpfs /run tmpfs mode=0755,nodev,nosuid,strictatime 0 0
tmpfs /var/volatile tmpfs defaults 0 0
EOF

# create dirs in tmpfs mounts
    cat >> ${IMAGE_ROOTFS}/etc/inittab << EOF

# Yocto compatibility fixes
null::sysinit:/bin/mkdir -p /var/volatile/log
null::sysinit:/bin/mkdir -p /var/volatile/tmp
null::sysinit:/bin/mkdir -p /run/lock
null::sysinit:/usr/bin/update-alternatives
null::sysinit:/bin/bash -c 'for script in /etc/rcS.d/*; do \$script start; done'
null::sysinit:/bin/bash -c 'for script in /etc/rc5.d/*; do \$script start; done'

EOF

    if [ ! -e ${IMAGE_ROOTFS}/http/config/en_US ]; then
        cd ${IMAGE_ROOTFS}/http/config
        ln -s . en_US
    fi

    if [ -e ${IMAGE_ROOTFS}/${sbindir}/i2cset -a ! -e ${IMAGE_ROOTFS}/usr/bin/i2cset ]; then
        ln -s ${sbindir}/i2cset ${IMAGE_ROOTFS}/usr/bin/i2cset
    fi

    if [ -e ${IMAGE_ROOTFS}/${base_sbindir}/fw_printenv -a ! -e ${IMAGE_ROOTFS}/usr/sbin/fw_printenv ]; then
        ln -s ${base_sbindir}/fw_printenv ${IMAGE_ROOTFS}/usr/sbin/fw_printenv
    fi

    if [ -e ${IMAGE_ROOTFS}/${base_sbindir}/fw_setenv -a ! -e ${IMAGE_ROOTFS}/usr/sbin/fw_setenv ]; then
        ln -s ${base_sbindir}/fw_setenv ${IMAGE_ROOTFS}/usr/sbin/fw_setenv
    fi

    if [ -e ${IMAGE_ROOTFS}/${base_sbindir}/mke2fs -a ! -e ${IMAGE_ROOTFS}/usr/sbin/mke2fs ]; then
        ln -s ${base_sbindir}/mke2fs ${IMAGE_ROOTFS}/usr/sbin/mke2fs
    fi

    if [ -e ${IMAGE_ROOTFS}/${bindir}/dnsmasq -a ! -e ${IMAGE_ROOTFS}/usr/sbin/dnsmasq ]; then
        ln -s ${bindir}/dnsmasq ${IMAGE_ROOTFS}/usr/sbin/dnsmasq
    fi

    if [ -e ${IMAGE_ROOTFS}/${bindir}/dhcp6c -a ! -e ${IMAGE_ROOTFS}/bin/dhcp6c ]; then
        ln -s ${bindir}/dhcp6c ${IMAGE_ROOTFS}/bin/dhcp6c
    fi

    chmod 4755 ${IMAGE_ROOTFS}/bin/ltrx_cli
    chmod 4755 ${IMAGE_ROOTFS}/usr/sbin/wpa_cli
    chmod 4755 ${IMAGE_ROOTFS}/bin/ser_con
    chmod 4755 ${IMAGE_ROOTFS}/usr/bin/traceroute.traceroute
    chmod 4755 ${IMAGE_ROOTFS}/usr/sbin/tcpdump
    chmod 4755 ${IMAGE_ROOTFS}/bin/ping.iputils
    chmod 4755 ${IMAGE_ROOTFS}/bin/ping6.iputils
    chmod 4755 ${IMAGE_ROOTFS}/sbin/ebtables
    chmod 4755 ${IMAGE_ROOTFS}/usr/sbin/iptables

    mkdir -p ${IMAGE_ROOTFS}/root

    chown --recursive --no-dereference root:root ${IMAGE_ROOTFS}

    find ${IMAGE_ROOTFS} \( -name ".gitkeep" -o -name ".gitignore" \) -exec rm {} \;
}
ROOTFS_POSTPROCESS_COMMAND += "apply_overlay; "
