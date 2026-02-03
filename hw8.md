1. Запустить Docker Desktop под администратором
2. Запустить IDE под администратором
3. mvn clean install -U <- собрать приложения
4. .\scripts\all-dockers-build.bat <- сборка всех докеров для заливки на dockerhub
5. .\scripts\all-dockers-image-push.bat <- размещение всех образов на dockerhub
6. https://hub.docker.com/repositories/mantogin?search=hw8- <- Проверить, что все докеры залились на докерхаб
7. kubectl create namespace mantogin-hw8
8. .\scripts\kubectl-apply-all-manifests.bat <- Применить все манифесты
9. minikube tunnel <- Запустить в отдельном окне 
10. newman run hw8-postman-collection.json