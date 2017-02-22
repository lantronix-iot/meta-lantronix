inherit image_types

IMAGE_DEPENDS_rom = "virtual/kernel:do_deploy \
                     ltrx-mkimage-native:do_build"

IMAGE_TYPEDEP_rom = "ubifs"

IMAGE_CMD_rom () {
    cd ${DEPLOY_DIR_IMAGE}

    cat ${KERNEL_IMAGETYPE} ${KERNEL_IMAGETYPE}-${KERNEL_DEVICETREE} > \
        ${KERNEL_IMAGETYPE}_w_dtb

    ltrx-mkimage -m -p ${LTRX_PRODUCT_FAMILY_ID} -t ${LTRX_KERNEL_IMAGE_TYPE} \
        -i ${LTRX_PRODUCT_CODE} \
        -r ${LTRX_PRODUCT_VERSION} -a ${LTRX_BASE_ADDRESS} \
        ${KERNEL_IMAGETYPE}_w_dtb

    ltrx-mkimage -m -p ${LTRX_PRODUCT_FAMILY_ID} -t ${LTRX_ROOTFS_IMAGE_TYPE} \
        -i ${LTRX_PRODUCT_CODE} \
        -r ${LTRX_PRODUCT_VERSION} -a ${LTRX_BASE_ADDRESS} \
        ${IMAGE_BASENAME}-${MACHINE}.ubifs

    ltrx-mkimage -c ${KERNEL_IMAGETYPE}_w_dtb.rom \
        ${IMAGE_BASENAME}-${MACHINE}.ubifs.rom \
        ${LTRX_PRODUCT_SHORT_NAME}_${IMAGE_BASENAME}_${LTRX_PRODUCT_VERSION}.rom
}

apply_overlay() {
    tar xvf ${LTRX_OVERLAY_DIR}/overlay.tar.xz -C ${IMAGE_ROOTFS}
    tar xvf ${LTRX_OVERLAY_DIR}/${MACHINE}-overlay.tar.xz -C ${IMAGE_ROOTFS}

    # un-expire account passwords
    sed -i 's/:0:0:99999/:10933:0:99999/' ${IMAGE_ROOTFS}/etc/shadow

    # override to use tmpfs instead of RAM disk
    install -D -m 0755 ${LTRX_SCRIPTS_DIR}/ltrx_remount_etc ${IMAGE_ROOTFS}/sbin/ltrx_remount_etc

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
