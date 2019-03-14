echo "Enonic XP:"
./validate-phrases -p /Users/sivesind/Documents/EnonicGIT/xp/xp/modules/app/app-system/src/main/resources/i18n
echo -e "\nApplications App:"
./validate-phrases -p /Users/sivesind/Documents/EnonicGIT/xp/apps/app-applications/src/main/resources/i18n/
echo -e "\nUsers App:"
./validate-phrases -p /Users/sivesind/Documents/EnonicGIT/xp/apps/app-users/src/main/resources/i18n/
echo -e "\nAdmin Home App:"
./validate-phrases -p /Users/sivesind/Documents/EnonicGIT/xp/apps/app-users/src/main/resources/i18n/
echo -e "\nStandard ID Provider App:"
./validate-phrases -p /Users/sivesind/Documents/EnonicGIT/xp/apps/app-standardidprovider/src/main/resources/i18n/
echo -e "\nLib Admin UI:"
./validate-phrases -p /Users/sivesind/Documents/EnonicGIT/xp/libs/lib-admin-ui/src/main/resources/i18n/ -f common.properties
echo -e "\nContent Studio App:"
./validate-phrases -p /Users/sivesind/Documents/EnonicGIT/xp/apps/contentstudio/src/main/resources/i18n/
